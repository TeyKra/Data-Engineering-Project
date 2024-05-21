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

// Objet DataToS3 pour uploader des données Kafka vers un bucket S3
object DataToS3 {
  private val logger = LoggerFactory.getLogger(DataToS3.getClass)

  // Configuration des propriétés pour Kafka Streams
  val props = new Properties()
  props.put(StreamsConfig.APPLICATION_ID_CONFIG, "iot-data-s3-uploader") // Identifiant de l'application
  props.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092") // Adresse du serveur Kafka
  props.put(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.String.getClass.getName) // Sérialiseur pour les clés
  props.put(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, Serdes.String.getClass.getName) // Sérialiseur pour les valeurs

  // Création d'un client S3 avec les paramètres de région et les informations d'identification par défaut
  val s3Client: S3Client = S3Client.builder()
    .region(Region.EU_NORTH_1) // Région du bucket S3
    .credentialsProvider(DefaultCredentialsProvider.create()) // Fournisseur d'identifiants par défaut
    .build()

  val bucketName = "efreidataengineering" // Nom du bucket S3

  // Fonction pour démarrer le flux Kafka pour uploader les données vers S3
  def startStream(): KafkaStreams = {
    val builder = new StreamsBuilder()
    // Création d'un flux (stream) à partir du topic "the_second_stream"
    val inputStream: KStream[String, String] = builder.stream[String, String]("the_second_stream")(Consumed.`with`(Serdes.String, Serdes.String))

    // Traitement de chaque message du flux
    inputStream.foreach { (_, value) =>
      uploadToS3(value) match {
        case Success(_) => logger.info("Data uploaded to S3 successfully.") // Log de succès d'upload
        case Failure(ex) => logger.error(s"Failed to upload data to S3: ${ex.getMessage}") // Log d'échec d'upload
      }
    }

    // Création et démarrage de l'instance KafkaStreams
    val streams = new KafkaStreams(builder.build(), props)
    streams.start()
    // Ajout d'un hook pour fermer proprement le flux et le client S3 lors de l'arrêt de l'application
    sys.ShutdownHookThread {
      streams.close()
      s3Client.close()
    }
    streams
  }

  // Fonction pour uploader les données vers S3
  def uploadToS3(data: String): Try[Unit] = {
    val putObjectRequest = PutObjectRequest.builder()
      .bucket(bucketName) // Nom du bucket S3
      .key(s"iot-data-${System.currentTimeMillis()}.json") // Clé de l'objet S3 avec un timestamp
      .build()

    val requestBody = RequestBody.fromString(data) // Corps de la requête S3 à partir de la chaîne de données
    Try {
      s3Client.putObject(putObjectRequest, requestBody) // Upload de l'objet vers S3
    }
  }
}
