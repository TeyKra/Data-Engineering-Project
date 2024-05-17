import org.apache.kafka.streams.scala.StreamsBuilder
import org.apache.kafka.streams.scala.kstream.{KStream, Consumed}
import org.apache.kafka.streams.scala.ImplicitConversions._
import org.apache.kafka.common.serialization.Serdes
import org.apache.kafka.streams.{KafkaStreams, StreamsConfig}
import java.util.Properties
import com.twilio.Twilio
import com.twilio.rest.api.v2010.account.Message
import com.twilio.`type`.PhoneNumber

object KafkaSmsSender {
  // Twilio Account SID, Auth Token, and Twilio phone number
  val accountSid = "AC44ff6c4f5f8aa4d339178fbf3e140bcd"
  val authToken = "d34b425f6cc2b50ae5cb9a2815779061"
  val fromNumber = "+12099201132"

  // Initialize Twilio
  Twilio.init(accountSid, authToken)

  def startSmsSenderStream(): KafkaStreams = {
    val props = new Properties()
    props.put(StreamsConfig.APPLICATION_ID_CONFIG, "iot-sms-sender")
    props.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092")
    props.put(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.String.getClass.getName)
    props.put(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, Serdes.String.getClass.getName)

    val builder = new StreamsBuilder()
    val inputStream: KStream[String, String] = builder.stream[String, String]("the_third_stream")(Consumed.`with`(Serdes.String, Serdes.String))

    inputStream.foreach { (_, value) =>
      IoTDataJson.deserialize(value) match {
        case Right(alertData) =>
          println(s"Alert detected for device: ${alertData.deviceId}. Sending SMS...")
          sendSms(alertData)
        case Left(error) =>
          println(s"Failed to deserialize IoTData: ${error.getMessage}")
      }
    }

    val streams = new KafkaStreams(builder.build(), props)
    streams.start()
    sys.ShutdownHookThread {
      streams.close()
    }
    streams
  }

  def sendSms(alertData: IoTData): Unit = {
    val toNumber = "+33669627003" // Remplacez par votre numéro de téléphone réel
    val messageBody = s"Alerte in: ${alertData.location.capital}: " +
      s"Qualité de l'air - CO2: ${alertData.qualiteAir.CO2}, Particules fines: ${alertData.qualiteAir.particulesFines}. " +
      s"Device ID: ${alertData.deviceId}"

    try {
      val message = Message.creator(
        new PhoneNumber(toNumber),
        new PhoneNumber(fromNumber),
        messageBody
      ).create()

      println(s"SMS sent successfully. SID: ${message.getSid}")
    } catch {
      case e: Exception => e.printStackTrace()
    }
  }
}