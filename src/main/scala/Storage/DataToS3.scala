import org.apache.kafka.streams.scala.StreamsBuilder
import org.apache.kafka.streams.scala.kstream.{KStream, Consumed}
import org.apache.kafka.streams.{KafkaStreams, StreamsConfig}
import org.apache.kafka.common.serialization.Serdes
import software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider
import software.amazon.awssdk.regions.Region
import software.amazon.awssdk.services.s3.S3Client
import software.amazon.awssdk.services.s3.model.PutObjectRequest
import software.amazon.awssdk.core.sync.RequestBody
import org.slf4j.LoggerFactory
import scala.util.{Try, Success, Failure}
import java.util.Properties

// DataToS3 object to upload Kafka data to an S3 bucket
object DataToS3 {
  private val logger = LoggerFactory.getLogger(DataToS3.getClass)

  // Configuring properties for Kafka Streams
  val props = new Properties()
  props.put(StreamsConfig.APPLICATION_ID_CONFIG, "iot-data-s3-uploader") // Application identifier
  props.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092") // Kafka server address
  props.put(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.String.getClass.getName) // Serializer for keys
  props.put(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, Serdes.String.getClass.getName) // Serializer for values

  // Creating an S3 client with default region settings and credentials
  val s3Client: S3Client = S3Client.builder()
    .region(Region.EU_NORTH_1) // S3 bucket region
    .credentialsProvider(DefaultCredentialsProvider.create()) // Default identifier provider
    .build()

  val bucketName = "efreidataengineering" // S3 bucket name

  // Function to start the Kafka flow to upload data to S3
  def startStream(): KafkaStreams = {
    val builder = new StreamsBuilder()
    // Creation of a stream from the topic "the_second_stream"
    val inputStream: KStream[String, String] = builder.stream[String, String]("the_second_stream")(Consumed.`with`(Serdes.String, Serdes.String))

    // Processing each message in the flow
    inputStream.foreach { (_, value) =>
      uploadToS3(value) match {
        case Success(_) => logger.info("Data uploaded to S3 successfully.") // Upload success log
        case Failure(ex) => logger.error(s"Failed to upload data to S3: ${ex.getMessage}") // Upload failure log
      }
    }

    // Creating and starting the KafkaStreams instance
    val streams = new KafkaStreams(builder.build(), props)
    streams.start()
    // Added a hook to cleanly close the flow and the S3 client when stopping the application
    sys.ShutdownHookThread {
      streams.close()
      s3Client.close()
    }
    streams
  }

  // Function to upload data to S3
  def uploadToS3(data: String): Try[Unit] = {
    val putObjectRequest = PutObjectRequest.builder()
      .bucket(bucketName) // S3 bucket name
      .key(s"iot-data-${System.currentTimeMillis()}.json") // Key of the S3 object with a timestamp
      .build()

    val requestBody = RequestBody.fromString(data) // S3 request body from data string
    Try {
      s3Client.putObject(putObjectRequest, requestBody) // Upload the object to S3
    }
  }
}
