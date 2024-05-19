import org.apache.kafka.streams.scala.StreamsBuilder
import org.apache.kafka.streams.scala.kstream.{KStream, Consumed}
import org.apache.kafka.streams.{KafkaStreams, StreamsConfig}
import org.apache.kafka.common.serialization.Serdes
import software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider
import software.amazon.awssdk.regions.Region
import software.amazon.awssdk.services.s3.S3Client
import software.amazon.awssdk.services.s3.model.PutObjectRequest
import software.amazon.awssdk.core.sync.RequestBody
import org.apache.spark.sql.{SparkSession, DataFrame}
import org.apache.spark.sql.functions._
import org.apache.pdfbox.pdmodel.{PDDocument, PDPage}
import org.apache.pdfbox.pdmodel.PDPageContentStream
import org.apache.pdfbox.pdmodel.font.PDType1Font
import org.apache.spark.sql.types._

import java.io.File
import java.nio.file.{Files, Paths}
import java.util.Properties

object S3ToData {
  val props = new Properties()
  props.put(StreamsConfig.APPLICATION_ID_CONFIG, "iot-data-s3-uploader")
  props.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092")
  props.put(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.String.getClass.getName)
  props.put(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, Serdes.String.getClass.getName)

  val s3Client: S3Client = S3Client.builder()
    .region(Region.EU_NORTH_1)
    .credentialsProvider(DefaultCredentialsProvider.create())
    .build()

  val bucketName = "efreidataengineering"
  val reportPath = "src/main/scala/Report"

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

  def startStream(): KafkaStreams = {
    val builder = new StreamsBuilder()
    val inputStream: KStream[String, String] = builder.stream[String, String]("the_second_stream")(Consumed.`with`(Serdes.String, Serdes.String))

    // Traiter chaque message individuellement
    inputStream.foreach { (_, value) =>
      uploadToS3(value)
    }

    val streams = new KafkaStreams(builder.build(), props)
    streams.start()
    sys.ShutdownHookThread {
      streams.close()
      s3Client.close()
    }
    streams
  }

  def uploadToS3(data: String): Unit = {
    val putObjectRequest = PutObjectRequest.builder()
      .bucket(bucketName)
      .key(s"iot-data-${System.currentTimeMillis()}.json")
      .build()

    val requestBody = RequestBody.fromString(data)
    try {
      s3Client.putObject(putObjectRequest, requestBody)
      println("Data uploaded to S3 successfully.")
    } catch {
      case e: Exception => println(s"Failed to upload data to S3: ${e.getMessage}")
    }
  }

  def loadNewDataToDataFrame(spark: SparkSession, iotDataFrame: DataFrame): DataFrame = {
    import spark.implicits._

    val s3Path = "s3a://efreidataengineering/" // Change this to your actual S3 path
    val newDF = spark.read.schema(iotDataSchema).json(s3Path)
    val updatedDF = iotDataFrame.union(newDF)

    // Perform analysis and generate PDF report
    generateReport(spark, updatedDF)

    updatedDF
  }

  def generateReport(spark: SparkSession, df: DataFrame): Unit = {
    import spark.implicits._

    val alertCount = df.groupBy("location.capital", "alerte").count()
    val topCO2Capitals = df.groupBy("location.capital")
      .agg(max("qualiteAir.CO2").alias("max_CO2"))
      .orderBy(desc("max_CO2"))
      .limit(3)
    val alertsByTimestamp = df.filter(col("alerte") === "Yes").select("timestamp", "location.capital", "qualiteAir.CO2", "qualiteAir.particulesFines")

    val latestTimestamp = df.agg(max("timestamp")).as[String].head()

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