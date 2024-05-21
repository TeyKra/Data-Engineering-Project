import org.apache.kafka.clients.producer.{KafkaProducer, ProducerRecord, ProducerConfig}
import java.util.Properties
import org.apache.kafka.clients.producer.{Callback, RecordMetadata}

// Objet MyKafkaProducer pour produire des messages Kafka
object MyKafkaProducer {
  // Configuration des propriétés du producteur Kafka
  val props = new Properties()
  props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092") // Adresse du serveur Kafka
  props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringSerializer") // Sérialiseur pour les clés
  props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringSerializer") // Sérialiseur pour les valeurs
  props.put(ProducerConfig.ACKS_CONFIG, "all") // Nécessite une confirmation complète de tous les réplicas avant de considérer un message comme envoyé
  props.put(ProducerConfig.DELIVERY_TIMEOUT_MS_CONFIG, "15000") // Timeout de livraison de 15 secondes
  props.put(ProducerConfig.LINGER_MS_CONFIG, "100") // Temps d'attente avant envoi des messages pour grouper les envois (100 millisecondes)
  props.put(ProducerConfig.REQUEST_TIMEOUT_MS_CONFIG, "5000") // Timeout de requête de 5 secondes

  // Création d'une instance de KafkaProducer avec les propriétés définies
  val producer = new KafkaProducer[String, String](props)

  // Méthode pour envoyer des données IoT à un topic Kafka
  def sendIoTData(topic: String, key: String, value: String): Unit = {
    // Création d'un enregistrement Kafka avec le topic, la clé et la valeur
    val record = new ProducerRecord[String, String](topic, key, value)
    // Envoi de l'enregistrement avec un callback pour gérer la réponse
    producer.send(record, new Callback {
      override def onCompletion(metadata: RecordMetadata, exception: Exception): Unit = {
        // Gestion des erreurs ou confirmation de succès
        Option(exception) match {
          case Some(ex) => println(s"Erreur lors de l'envoi à Kafka: ${ex.getMessage}") // Affiche l'erreur si elle existe
          case None => println(s"Message envoyé avec succès au topic ${metadata.topic()} partition ${metadata.partition()} offset ${metadata.offset()}") // Confirmation de l'envoi avec les détails du message
        }
      }
    })
  }

  // Méthode pour fermer le producteur Kafka
  def close(): Unit = {
    producer.close()
  }
}