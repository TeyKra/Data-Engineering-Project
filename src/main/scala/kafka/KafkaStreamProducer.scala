import org.apache.kafka.clients.producer.{KafkaProducer, ProducerRecord, ProducerConfig}
import java.util.Properties
import org.apache.kafka.clients.producer.{Callback, RecordMetadata}

// MyKafkaProducer object to produce Kafka messages
object MyKafkaProducer {
  // Configuring Kafka producer properties
  val props = new Properties()
  props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092") // Kafka server address
  props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringSerializer") // Serializer for keys
  props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringSerializer") // Serializer for values
  props.put(ProducerConfig.ACKS_CONFIG, "all") // Requires full confirmation of all replicas before considering a message sent
  props.put(ProducerConfig.DELIVERY_TIMEOUT_MS_CONFIG, "15000") // Delivery timeout of 15 seconds
  props.put(ProducerConfig.LINGER_MS_CONFIG, "100") // Waiting time before sending messages to group sendings (100 milliseconds)
  props.put(ProducerConfig.REQUEST_TIMEOUT_MS_CONFIG, "5000") // Query timeout of 5 seconds

  // Create a KafkaProducer instance with defined properties
  val producer = new KafkaProducer[String, String](props)

  // Method to send IoT data to a Kafka topic
  def sendIoTData(topic: String, key: String, value: String): Unit = {
    // Create a Kafka record with topic, key and value
    val record = new ProducerRecord[String, String](topic, key, value)
    // Send the recording with a callback to handle the response
    producer.send(record, new Callback {
      override def onCompletion(metadata: RecordMetadata, exception: Exception): Unit = {
        // Error handling or confirmation of success
        Option(exception) match {
          case Some(ex) => println(s"Error sending to Kafka: ${ex.getMessage}") // Show the error if it exists
          case None => println(s"Message successfully sent to topic ${metadata.topic()} partition ${metadata.partition()} offset ${metadata.offset()}") // Confirmation of sending with message details
        }
      }
    })
  }

  // Method to close the Kafka producer
  def close(): Unit = {
    producer.close()
  }
}