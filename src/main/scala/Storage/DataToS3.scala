import org.apache.kafka.streams.scala.StreamsBuilder
import org.apache.kafka.streams.scala.kstream.{KStream, Consumed}
import org.apache.kafka.streams.{KafkaStreams, StreamsConfig}
import org.apache.kafka.common.serialization.Serdes
import software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider
import software.amazon.awssdk.regions.Region
import software.amazon.awssdk.services.s3.S3Client
import software.amazon.awssdk.services.s3.model.PutObjectRequest
import software.amazon.awssdk.core.sync.RequestBody

import java.util.Properties

object DataToS3 {
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
}
