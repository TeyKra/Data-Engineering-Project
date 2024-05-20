import org.apache.kafka.clients.producer.{KafkaProducer, ProducerRecord, ProducerConfig}
import java.util.Properties
import org.apache.kafka.clients.producer.{Callback, RecordMetadata}

object MyKafkaProducer {
  val props = new Properties()
  props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092")
  props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringSerializer")
  props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringSerializer")
  props.put(ProducerConfig.ACKS_CONFIG, "all")
  props.put(ProducerConfig.DELIVERY_TIMEOUT_MS_CONFIG, "15000") // 15 secondes
  props.put(ProducerConfig.LINGER_MS_CONFIG, "100")            // 100 millisecondes
  props.put(ProducerConfig.REQUEST_TIMEOUT_MS_CONFIG, "5000")  // 5 secondes

  val producer = new KafkaProducer[String, String](props)

  def sendIoTData(topic: String, key: String, value: String): Unit = {
    val record = new ProducerRecord[String, String](topic, key, value)
    producer.send(record, new Callback {
      override def onCompletion(metadata: RecordMetadata, exception: Exception): Unit = {
        Option(exception) match {
          case Some(ex) => println(s"Erreur lors de l'envoi à Kafka: ${ex.getMessage}")
          case None => println(s"Message envoyé avec succès au topic ${metadata.topic()} partition ${metadata.partition()} offset ${metadata.offset()}")
        }
      }
    })
  }
  
  def close(): Unit = {
    producer.close()
  }
}