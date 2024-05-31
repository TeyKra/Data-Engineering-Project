import org.apache.kafka.streams.scala.StreamsBuilder
import org.apache.kafka.streams.scala.kstream.{KStream, Consumed, Produced}
import org.apache.kafka.streams.scala.ImplicitConversions._
import org.apache.kafka.common.serialization.Serdes
import org.apache.kafka.streams.{KafkaStreams, StreamsConfig}
import java.util.Properties

object KafkaStreamProcessing {
  // Function to start Kafka stream processing
  def startStream(): KafkaStreams = {
    // Configuring properties for Kafka Streams
    val props = new Properties()
    props.put(StreamsConfig.APPLICATION_ID_CONFIG, "iot-data-processor") // Application identifier
    props.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092") // Kafka server address
    props.put(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.String.getClass.getName) // Serializer for keys
    props.put(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, Serdes.String.getClass.getName) // Serializer for values

    // Creation of a StreamsBuilder to build the stream processing topology
    val builder = new StreamsBuilder()
    // Creation of a stream from the topic "the_stream"
    val stream: KStream[String, String] = builder.stream[String, String]("the_stream")(Consumed.`with`(Serdes.String, Serdes.String))

    // Transform the flow by updating the values
    val updatedStream = stream.mapValues { value =>
      // Deserialization of IoT data and update of the alert if necessary
      IoTDataJson.deserialize(value) match {
        case Right(data) => IoTDataJson.serialize(updateAlerte(data)) // Serialization of updated data
        case Left(error) =>
          println(s"Deserialization error: ${error.getMessage}") // Display deserialization error
          value // Return the original value on error
      }
    }

    // Writing the transformed stream to a new topic "the_second_stream"
    updatedStream.to("the_second_stream")(Produced.`with`(Serdes.String, Serdes.String))

    // Creating and starting the KafkaStreams instance
    val streams = new KafkaStreams(builder.build(), props)
    streams.start()
    // Added a hook to cleanly close the flow when stopping the application
    sys.ShutdownHookThread {
      streams.close()
    }
    streams
  }

  // Function to update alert status in IoT data
  def updateAlerte(iotData: IoTData): IoTData = {
    val co2Alert = iotData.qualiteAir.CO2 >= 300000000 // Checking the CO2 threshold
    val particulesFinesAlert = iotData.qualiteAir.particulesFines >= 100 // Checking the fine particle threshold
    // Update alert status if one of the thresholds is exceeded
    if (co2Alert || particulesFinesAlert) {
      iotData.copy(alerte = "Yes") // Copy the IoTData object with updated alert status
    } else {
      iotData // Return the IoTData object without modification
    }
  }
}