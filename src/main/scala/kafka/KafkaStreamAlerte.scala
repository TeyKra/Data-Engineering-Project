import org.apache.kafka.streams.scala.StreamsBuilder
import org.apache.kafka.streams.scala.kstream.{KStream, Consumed, Produced}
import org.apache.kafka.streams.scala.ImplicitConversions._
import org.apache.kafka.common.serialization.Serdes
import org.apache.kafka.streams.{KafkaStreams, StreamsConfig}
import java.util.Properties

object KafkaAlertFilter {
  def main(args: Array[String]): Unit = {
    val props = new Properties()
    props.put(StreamsConfig.APPLICATION_ID_CONFIG, "iot-alert-filter")
    props.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092")
    props.put(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.String.getClass.getName)
    props.put(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, Serdes.String.getClass.getName)

    val builder = new StreamsBuilder()
    val inputStream: KStream[String, String] = builder.stream[String, String]("iot-output-topic")(Consumed.`with`(Serdes.String, Serdes.String))

    val filteredStream = inputStream
      .mapValues(value => IoTDataJson.deserialize(value))
      .filter((_, data) => data.isRight && data.right.get.alerte == "Yes")
      .mapValues(data => IoTDataJson.serialize(data.right.get))

    filteredStream.to("iot-alerted-topic")(Produced.`with`(Serdes.String, Serdes.String))

    val streams = new KafkaStreams(builder.build(), props)
    streams.start()

    sys.ShutdownHookThread {
      streams.close()
    }
  }
}
