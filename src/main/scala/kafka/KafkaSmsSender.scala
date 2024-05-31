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

// KafkaSmsSender object to send SMS in case of alert detected in a Kafka flow
object KafkaSmsSender {
  private val logger = LoggerFactory.getLogger(KafkaSmsSender.getClass)

  // Twilio identifiers for sending SMS
  val accountSid = "..."
  val authToken = "..."
  val fromNumber = "+..." // Twilio phone number for sending SMS

  // Initializing Twilio with identifiers
  Twilio.init(accountSid, authToken)

  // Function to start Kafka flow for sending SMS
  def startSmsSenderStream(): KafkaStreams = {
    val props = new Properties()
    props.put(StreamsConfig.APPLICATION_ID_CONFIG, "iot-sms-sender") // Application identifier
    props.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092") // Kafka server address
    props.put(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.String.getClass.getName) // Serializer for keys
    props.put(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, Serdes.String.getClass.getName) // Serializer for values

    val builder = new StreamsBuilder()
    // Creation of a stream from the topic "the_third_stream"
    val inputStream: KStream[String, String] = builder.stream[String, String]("the_third_stream")(Consumed.`with`(Serdes.String, Serdes.String))

    // Processing flow messages to detect alerts and send SMS
    inputStream.foreach { (_, value) =>
      IoTDataJson.deserialize(value) match {
        case Right(alertData) =>
          logger.info(s"Alert detected for device: ${alertData.deviceId}. Sending SMS...") // Alert log detected
          sendSms(alertData) match {
            case Success(sid) => logger.info(s"SMS sent successfully. SID: $sid") // SMS sending success log
            case Failure(ex) => logger.error(s"Failed to send SMS: ${ex.getMessage}") // SMS sending failure log
          }
        case Left(error) =>
          logger.error(s"Failed to deserialize IoTData: ${error.getMessage}") // Deserialization error log
      }
    }

    // Creating and starting the KafkaStreams instance
    val streams = new KafkaStreams(builder.build(), props)
    streams.start()
    // Added a hook to cleanly close the flow when stopping the application
    sys.ShutdownHookThread {
      streams.close()
    }
    streams
  }

  // Function to send an SMS with IoT alert data
  def sendSms(alertData: IoTData): Try[String] = {
    val toNumber = "+..." // Destination phone number
    val messageBody = s"Alerte in: ${alertData.location.capital}: " +
      s"Air quality - CO2: ${alertData.qualiteAir.CO2}, Fine particles: ${alertData.qualiteAir.particulesFines}. " +
      s"Device ID: ${alertData.deviceId}" // Body of the SMS message with alert details

    Try {
      val message = Message.creator(
        new PhoneNumber(toNumber),
        new PhoneNumber(fromNumber),
        messageBody
      ).create()

      message.getSid // Return the SID of the message on success
    }
  }
}