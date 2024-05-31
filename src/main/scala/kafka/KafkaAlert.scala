import org.apache.kafka.streams.scala.StreamsBuilder
import org.apache.kafka.streams.scala.kstream.{KStream, Consumed, Produced}
import org.apache.kafka.streams.scala.ImplicitConversions._
import org.apache.kafka.common.serialization.Serdes
import org.apache.kafka.streams.{KafkaStreams, StreamsConfig}
import java.util.Properties

object KafkaAlert {
  // Function to start the Kafka flow to detect alerts
  def startAlertStream(): KafkaStreams = {
    // Configuring properties for Kafka Streams
    val props = new Properties()
    props.put(StreamsConfig.APPLICATION_ID_CONFIG, "iot-alert-emailer") // Application identifier
    props.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092") // Kafka server address
    props.put(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.String.getClass.getName) // Serializer for keys
    props.put(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, Serdes.String.getClass.getName) // Serializer for values

    // Creating a StreamsBuilder to build the stream processing topology
    val builder = new StreamsBuilder()
    // Creation of a stream from the topic "the_second_stream"
    val inputStream: KStream[String, String] = builder.stream[String, String]("the_second_stream")(Consumed.`with`(Serdes.String, Serdes.String))

    // Flow processing to filter messages with alert
    val alertStream = inputStream
      .mapValues(value => IoTDataJson.deserialize(value)) // Deserialization of JSON values
      .filter((_, data) => data.fold(_ => false, _.alerte == "Yes")) // Filtering messages with alert

    // Transformation of alerts into JSON and sending to a new topic "the_third_stream"
    alertStream.mapValues(_.fold(error => "", data => IoTDataJson.serialize(data)))
      .to("the_third_stream")(Produced.`with`(Serdes.String, Serdes.String))

    // Creating and starting the KafkaStreams instance
    val streams = new KafkaStreams(builder.build(), props)
    streams.start()
    // Added a hook to cleanly close the flow when stopping the application
    sys.ShutdownHookThread {
      streams.close()
    }
    streams
  }
}