import org.apache.kafka.streams.scala.StreamsBuilder
import org.apache.kafka.streams.scala.kstream.{KStream, Consumed}
import org.apache.kafka.streams.scala.ImplicitConversions._
import org.apache.kafka.common.serialization.Serdes
import org.apache.kafka.streams.{KafkaStreams, StreamsConfig}
import java.util.Properties
import com.twilio.Twilio
import com.twilio.rest.api.v2010.account.Message
import com.twilio.`type`.PhoneNumber
import org.slf4j.LoggerFactory
import scala.util.{Try, Success, Failure}

// Objet KafkaSmsSender pour envoyer des SMS en cas d'alerte détectée dans un flux Kafka
object KafkaSmsSender {
  private val logger = LoggerFactory.getLogger(KafkaSmsSender.getClass)

  // Identifiants Twilio pour l'envoi de SMS
  val accountSid = "AC44ff6c4f5f8aa4d339178fbf3e140bcd"
  val authToken = "d34b425f6cc2b50ae5cb9a2815779061"
  val fromNumber = "+12099201132" // Numéro de téléphone Twilio pour l'envoi des SMS

  // Initialisation de Twilio avec les identifiants
  Twilio.init(accountSid, authToken)

  // Fonction pour démarrer le flux Kafka pour l'envoi de SMS
  def startSmsSenderStream(): KafkaStreams = {
    val props = new Properties()
    props.put(StreamsConfig.APPLICATION_ID_CONFIG, "iot-sms-sender") // Identifiant de l'application
    props.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092") // Adresse du serveur Kafka
    props.put(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.String.getClass.getName) // Sérialiseur pour les clés
    props.put(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, Serdes.String.getClass.getName) // Sérialiseur pour les valeurs

    val builder = new StreamsBuilder()
    // Création d'un flux (stream) à partir du topic "the_third_stream"
    val inputStream: KStream[String, String] = builder.stream[String, String]("the_third_stream")(Consumed.`with`(Serdes.String, Serdes.String))

    // Traitement des messages du flux pour détecter les alertes et envoyer des SMS
    inputStream.foreach { (_, value) =>
      IoTDataJson.deserialize(value) match {
        case Right(alertData) =>
          logger.info(s"Alert detected for device: ${alertData.deviceId}. Sending SMS...") // Log d'alerte détectée
          sendSms(alertData) match {
            case Success(sid) => logger.info(s"SMS sent successfully. SID: $sid") // Log de succès d'envoi de SMS
            case Failure(ex) => logger.error(s"Failed to send SMS: ${ex.getMessage}") // Log d'échec d'envoi de SMS
          }
        case Left(error) =>
          logger.error(s"Failed to deserialize IoTData: ${error.getMessage}") // Log d'erreur de désérialisation
      }
    }

    // Création et démarrage de l'instance KafkaStreams
    val streams = new KafkaStreams(builder.build(), props)
    streams.start()
    // Ajout d'un hook pour fermer proprement le flux lors de l'arrêt de l'application
    sys.ShutdownHookThread {
      streams.close()
    }
    streams
  }

  // Fonction pour envoyer un SMS avec les données d'alerte IoT
  def sendSms(alertData: IoTData): Try[String] = {
    val toNumber = "+33669627003" // Numéro de téléphone de destination
    val messageBody = s"Alerte in: ${alertData.location.capital}: " +
      s"Qualité de l'air - CO2: ${alertData.qualiteAir.CO2}, Particules fines: ${alertData.qualiteAir.particulesFines}. " +
      s"Device ID: ${alertData.deviceId}" // Corps du message SMS avec les détails de l'alerte

    Try {
      val message = Message.creator(
        new PhoneNumber(toNumber),
        new PhoneNumber(fromNumber),
        messageBody
      ).create()

      message.getSid // Retourne le SID du message en cas de succès
    }
  }
}