import org.apache.spark.sql.{SparkSession, DataFrame}
import org.apache.spark.sql.functions._
import org.apache.spark.sql.types._
import software.amazon.awssdk.services.s3.S3Client
import software.amazon.awssdk.services.s3.model.ListObjectsV2Request
import software.amazon.awssdk.regions.Region
import software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider

import scala.jdk.CollectionConverters._
import java.nio.file.{Files, Paths}
import org.apache.pdfbox.pdmodel.{PDDocument, PDPage}
import org.apache.pdfbox.pdmodel.PDPageContentStream
import org.apache.pdfbox.pdmodel.font.PDType1Font

object S3ToData {
  // Creating an S3 client with default region settings and credentials
  val s3Client: S3Client = S3Client.builder()
    .region(Region.EU_NORTH_1)
    .credentialsProvider(DefaultCredentialsProvider.create())
    .build()

  val bucketName = "Your Bucket Name" // S3 bucket name

  // Definition of the IoT data schema
  val iotDataSchema = StructType(Seq(
    StructField("timestamp", StringType, true),
    StructField("deviceId", StringType, true),
    StructField("location", StructType(Seq(
      StructField("capital", StringType, true),
      StructField("latitude", DoubleType, true),
      StructField("longitude", DoubleType, true)
    )), true),
    StructField("qualiteAir", StructType(Seq(
      StructField("CO2", DoubleType, true),
      StructField("particulesFines", DoubleType, true)
    )), true),
    StructField("niveauxSonores", DoubleType, true),
    StructField("temperature", DoubleType, true),
    StructField("humidite", DoubleType, true),
    StructField("alerte", StringType, true)
  ))

  // Function to load new data into a Spark DataFrame and update the processed files
  def loadNewDataToDataFrame(spark: SparkSession, iotDataFrame: DataFrame, processedFiles: Set[String]): (DataFrame, Set[String]) = {
    import spark.implicits._

    val (newFiles, updatedProcessedFiles) = listNewFiles(processedFiles) // List of new files

    if (newFiles.nonEmpty) {
      // Reading new JSON files with the defined schema
      val newDF = spark.read.schema(iotDataSchema).json(newFiles: _*)

      // Filter rows already present in the existing DataFrame
      val existingData = iotDataFrame.select("deviceId", "timestamp").as[(String, String)].collect().toSet
      val newUniqueDF = newDF.filter(row => !existingData.contains((row.getAs[String]("deviceId"), row.getAs[String]("timestamp"))))

      // Union of the existing DataFrame with the new unique data
      val updatedDF = iotDataFrame.union(newUniqueDF)
        .dropDuplicates("deviceId", "timestamp")
        .orderBy("timestamp", "deviceId")

      generateReport(spark, updatedDF) // Generation of the report

      (updatedDF, updatedProcessedFiles)
    } else {
      (iotDataFrame, processedFiles) // Return the unchanged DataFrame and processed files
    }
  }

  // Function to list new files in the S3 bucket
  def listNewFiles(processedFiles: Set[String]): (Seq[String], Set[String]) = {
    val listObjectsRequest = ListObjectsV2Request.builder()
      .bucket(bucketName)
      .build()

    val listObjectsResponse = s3Client.listObjectsV2(listObjectsRequest)
    val allFiles = listObjectsResponse.contents().asScala.map(_.key()).filter(_.endsWith(".json")).toSeq

    val newFiles = allFiles.filterNot(processedFiles.contains).map(file => s"s3a://$bucketName/$file")
    val updatedProcessedFiles = processedFiles ++ newFiles.map(_.split("/").last)

    (newFiles, updatedProcessedFiles)
  }

  // Function to generate a PDF report from IoT data
  def generateReport(spark: SparkSession, df: DataFrame): Unit = {
    import spark.implicits._

    // Count alerts by capital and alert type
    val alertCount = df.groupBy("location.capital", "alerte").count()
    // Obtain the 3 capitals with the highest CO2 levels
    val topCO2Capitals = df.groupBy("location.capital")
      .agg(max("qualiteAir.CO2").alias("max_CO2"))
      .orderBy(desc("max_CO2"))
      .limit(3)
    // Filter alerts by timestamp
    val alertsByTimestamp = df.filter(col("alerte") === "Yes").select("timestamp", "location.capital", "qualiteAir.CO2", "qualiteAir.particulesFines")

    // Get the latest timestamp of the data
    val latestTimestamp = df.agg(max("timestamp")).as[String].take(1).headOption.getOrElse("unknown")

    // Path and name of the PDF report file
    val reportPath = "src/main/scala/Report"
    val reportFile = Paths.get(s"$reportPath/report_$latestTimestamp.pdf")
    if (!Files.exists(reportFile.getParent)) {
      Files.createDirectories(reportFile.getParent)
    }

    // Creating a PDF document with PDFBox
    val document = new PDDocument()
    val page = new PDPage()
    document.addPage(page)

    val contentStream = new PDPageContentStream(document, page)
    contentStream.setFont(PDType1Font.HELVETICA, 12)

    // Writing the content of the PDF report
    contentStream.beginText()
    contentStream.newLineAtOffset(50, 750)
    contentStream.showText(s"Report generated at: $latestTimestamp")
    contentStream.newLineAtOffset(0, -20)
    contentStream.showText("Alert Counts by Country:")
    contentStream.newLineAtOffset(0, -20)

    alertCount.collect().foreach { row =>
      contentStream.showText(s"${row.getString(0)} - ${row.getString(1)}: ${row.getLong(2)}")
      contentStream.newLineAtOffset(0, -20)
    }

    contentStream.newLineAtOffset(0, -20)
    contentStream.showText("Top 3 Capitals with Highest CO2 Levels:")
    contentStream.newLineAtOffset(0, -20)

    topCO2Capitals.collect().foreach { row =>
      contentStream.showText(s"${row.getString(0)}: ${row.getDouble(1)} ppm")
      contentStream.newLineAtOffset(0, -20)
    }

    contentStream.newLineAtOffset(0, -20)
    contentStream.showText("Alerts by Timestamp:")
    contentStream.newLineAtOffset(0, -20)

    alertsByTimestamp.collect().foreach { row =>
      contentStream.showText(s"${row.getString(0)} - ${row.getString(1)}: CO2 ${row.getDouble(2)} ppm, Fine particles ${row.getDouble(3)} µg/m³")
      contentStream.newLineAtOffset(0, -20)
    }

    contentStream.endText()
    contentStream.close()

    // Save the PDF document
    document.save(reportFile.toFile)
    document.close()

    println(s"Report saved to: $reportFile")
  }
}