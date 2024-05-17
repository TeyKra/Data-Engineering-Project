import org.apache.kafka.streams.scala.StreamsBuilder
import org.apache.kafka.streams.scala.kstream.{KStream, Consumed, Produced}
import org.apache.kafka.streams.scala.ImplicitConversions._
import org.apache.kafka.common.serialization.Serdes
import org.apache.kafka.streams.{KafkaStreams, StreamsConfig}
import java.util.Properties

object KafkaStreamProcessing {
  def startStream(): KafkaStreams = {
    val props = new Properties()
    props.put(StreamsConfig.APPLICATION_ID_CONFIG, "iot-data-processor")
    props.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092")
    props.put(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.String.getClass.getName)
    props.put(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, Serdes.String.getClass.getName)

    val builder = new StreamsBuilder()
    val stream: KStream[String, String] = builder.stream[String, String]("the_stream")(Consumed.`with`(Serdes.String, Serdes.String))

    val updatedStream = stream.mapValues { value =>
      IoTDataJson.deserialize(value) match {
        case Right(data) => IoTDataJson.serialize(updateAlerte(data))
        case Left(error) =>
          println(s"Deserialization error: ${error.getMessage}")
          value
      }
    }

    updatedStream.to("the_second_stream")(Produced.`with`(Serdes.String, Serdes.String))

    val streams = new KafkaStreams(builder.build(), props)
    streams.start()
    sys.ShutdownHookThread {
      streams.close()
    }
    streams
  }

  def updateAlerte(iotData: IoTData): IoTData = {
    val co2Alert = iotData.qualiteAir.CO2 >= 300000000
    val particulesFinesAlert = iotData.qualiteAir.particulesFines >= 100
    if (co2Alert || particulesFinesAlert) {
      iotData.copy(alerte = "Yes")
    } else {
      iotData
    }
  }
}