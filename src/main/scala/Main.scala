import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import scala.util.{Random, Try, Success, Failure}
import scala.concurrent.duration._
import org.apache.spark.sql.{SparkSession, DataFrame} 

object Main extends App {
  println("IoT Data Reports:")
  println("=" * 75)  // Show a separator line

  // Initialize a Spark session with S3 configuration
  val spark = SparkSession.builder
    .appName("S3ToData")
    .master("local[*]") // Using all locally available cores
    .config("spark.hadoop.fs.s3a.impl", "org.apache.hadoop.fs.s3a.S3AFileSystem")
    .config("spark.hadoop.fs.s3a.aws.credentials.provider", "com.amazonaws.auth.DefaultAWSCredentialsProviderChain")
    .config("spark.hadoop.fs.s3a.endpoint", "s3.amazonaws.com")
    .getOrCreate()

  import spark.implicits._
  // Initialize an empty DataFrame for IoTData
  val initialIoTDataFrame: DataFrame = spark.emptyDataset[IoTData].toDF()
  val initialProcessedFiles = Set[String]()  // Set to keep track of processed files

  // Start Kafka stream processing
  KafkaStreamProcessing.startStream() // Start processing the Kafka stream
  KafkaAlert.startAlertStream()       // Start the alert stream
  KafkaSmsSender.startSmsSenderStream() // Start the stream for sending SMS
  DataToS3.startStream()              // Start the stream for upload to S3

  // Add a hook to close resources when closing the application
  sys.addShutdownHook {
    MyKafkaProducer.close()
    DataToS3.s3Client.close()
    println("Closing the Kafka producer.")
  }

  // Launch the simulation continuously
  val start = LocalDateTime.now() // Start time

  // Function to run the simulation continuously
  def runSimulation(currentIoTDataFrame: DataFrame, currentProcessedFiles: Set[String]): Unit = {
    val currentTime = LocalDateTime.now() // Actual hour
    // For each location and its index
    Capitales.localisations.zipWithIndex.foreach { case (loc, index) =>
      val deviceId = s"device${100 + index}" // Unique ID for each device
      val alerte = "No" // Initialize the alert to "No"
      val rapport = SimulateurIoT.simulerRapportIoT(deviceId, currentTime, loc, alerte) // Generation of the report

      println("------------JSON serialization------------")
      val json = IoTDataJson.serialize(rapport)
      println(s"\nJSON:\n$json")

      Try {
        MyKafkaProducer.sendIoTData("the_stream", rapport.deviceId, json) // Sending IoT data to Kafka
      } match {
        case Success(_) => // Do nothing or log success
        case Failure(e) => println(s"Error sending to Kafka: ${e.getMessage}")
      }

      val deserializedData = IoTDataJson.deserialize(json) // Deserialization of JSON data
      deserializedData match {
        case Right(data) =>
          println("\n------------JSON deserialization------------")
          println(s"IoT report for device ${data.deviceId} just now ${data.timestamp}")
          println(s"Location : ${data.location.capital}, Latitude ${data.location.latitude}°, Longitude ${data.location.longitude}°")
          println(s"Air quality : CO2 ${data.qualiteAir.CO2} ppm, Fine particles ${data.qualiteAir.particulesFines} µg/m³")
          println(s"Sound Levels: ${data.niveauxSonores} dB")
          println(s"Temperature : ${data.temperature}°C")
          println(s"Humidity : ${data.humidite}%")
          println(s"Alert : ${data.alerte}")
        case Left(error) =>
          println(s"Failed to deserialize: ${error.getMessage}")
      }

      println("\n------------Serialisation CSV------------")
      val csv = IoTDataCsv.serializeToCsv(Seq(rapport))
      println(s"\nCSV:\n$csv")

      println("\n------------Désérialisation CSV------------")
      val deserializedCsvData = IoTDataCsv.deserializeFromCsv(csv)
      deserializedCsvData.foreach { data =>
        println(s"IoT report for device ${data.deviceId} just now ${data.timestamp}")
        println(s"Location : ${data.location.capital}, Latitude ${data.location.latitude}°, Longitude ${data.location.longitude}°")
        println(s"Air quality : CO2 ${data.qualiteAir.CO2} ppm, Fine particles ${data.qualiteAir.particulesFines} µg/m³")
        println(s"Sound Levels: ${data.niveauxSonores} dB")
        println(s"Temperature : ${data.temperature}°C")
        println(s"Humidity : ${data.humidite}%")
        println(s"Alert : ${data.alerte}")
      }

      println("-" * 100) // Dividing line between reports
    }

    // Load new data from S3 and add it to the DataFrame
    val (updatedDataFrame, updatedProcessedFiles) = S3ToData.loadNewDataToDataFrame(spark, currentIoTDataFrame, currentProcessedFiles)

    updatedDataFrame.show(false) // Show data to check

    Thread.sleep(6000) // Pause for 1 minute

    runSimulation(updatedDataFrame, updatedProcessedFiles) // Recursive call with new values
  }

  // Start simulation
  runSimulation(initialIoTDataFrame, initialProcessedFiles)
}