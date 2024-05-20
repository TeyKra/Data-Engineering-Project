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
  val s3Client: S3Client = S3Client.builder()
    .region(Region.EU_NORTH_1)
    .credentialsProvider(DefaultCredentialsProvider.create())
    .build()

  val bucketName = "efreidataengineering"
  var processedFiles = Set[String]()  // Set to keep track of processed files

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

  def loadNewDataToDataFrame(spark: SparkSession, iotDataFrame: DataFrame): DataFrame = {
    import spark.implicits._

    val newFiles = listNewFiles()

    if (newFiles.nonEmpty) {
      val newDF = spark.read.schema(iotDataSchema).json(newFiles: _*)

      // Filter out rows that already exist in the existing DataFrame
      val existingData = iotDataFrame.select("deviceId", "timestamp").as[(String, String)].collect().toSet
      val newUniqueDF = newDF.filter(row => !existingData.contains((row.getAs[String]("deviceId"), row.getAs[String]("timestamp"))))

      val updatedDF = iotDataFrame.union(newUniqueDF)
        .dropDuplicates("deviceId", "timestamp")
        .orderBy("timestamp", "deviceId")

      processedFiles ++= newFiles.toSet

      generateReport(spark, updatedDF)

      updatedDF
    } else {
      iotDataFrame
    }
  }

  def listNewFiles(): Seq[String] = {
    val listObjectsRequest = ListObjectsV2Request.builder()
      .bucket(bucketName)
      .build()

    val listObjectsResponse = s3Client.listObjectsV2(listObjectsRequest)
    val allFiles = listObjectsResponse.contents().asScala.map(_.key()).filter(_.endsWith(".json")).toSeq

    allFiles.filterNot(processedFiles.contains).map(file => s"s3a://$bucketName/$file")
  }

  def generateReport(spark: SparkSession, df: DataFrame): Unit = {
    import spark.implicits._

    val alertCount = df.groupBy("location.capital", "alerte").count()
    val topCO2Capitals = df.groupBy("location.capital")
      .agg(max("qualiteAir.CO2").alias("max_CO2"))
      .orderBy(desc("max_CO2"))
      .limit(3)
    val alertsByTimestamp = df.filter(col("alerte") === "Yes").select("timestamp", "location.capital", "qualiteAir.CO2", "qualiteAir.particulesFines")

    val latestTimestamp = df.agg(max("timestamp")).as[String].take(1).headOption.getOrElse("unknown")

    val reportPath = "src/main/scala/Report"
    val reportFile = Paths.get(s"$reportPath/report_$latestTimestamp.pdf")
    if (!Files.exists(reportFile.getParent)) {
      Files.createDirectories(reportFile.getParent)
    }

    val document = new PDDocument()
    val page = new PDPage()
    document.addPage(page)

    val contentStream = new PDPageContentStream(document, page)
    contentStream.setFont(PDType1Font.HELVETICA, 12)

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
      contentStream.showText(s"${row.getString(0)} - ${row.getString(1)}: CO2 ${row.getDouble(2)} ppm, Particules Fines ${row.getDouble(3)} µg/m³")
      contentStream.newLineAtOffset(0, -20)
    }

    contentStream.endText()
    contentStream.close()

    document.save(reportFile.toFile)
    document.close()

    println(s"Report saved to: $reportFile")
  }
}