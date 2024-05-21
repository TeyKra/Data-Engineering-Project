import org.apache.kafka.streams.scala.StreamsBuilder
import org.apache.kafka.streams.scala.kstream.{KStream, Consumed, Produced}
import org.apache.kafka.streams.scala.ImplicitConversions._
import org.apache.kafka.common.serialization.Serdes
import org.apache.kafka.streams.{KafkaStreams, StreamsConfig}
import java.util.Properties

object KafkaStreamProcessing {
  // Fonction pour démarrer le traitement du flux Kafka
  def startStream(): KafkaStreams = {
    // Configuration des propriétés pour Kafka Streams
    val props = new Properties()
    props.put(StreamsConfig.APPLICATION_ID_CONFIG, "iot-data-processor") // Identifiant de l'application
    props.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092") // Adresse du serveur Kafka
    props.put(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.String.getClass.getName) // Sérialiseur pour les clés
    props.put(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, Serdes.String.getClass.getName) // Sérialiseur pour les valeurs

    // Création d'un StreamsBuilder pour construire le topologie de traitement de flux
    val builder = new StreamsBuilder()
    // Création d'un flux (stream) à partir du topic "the_stream"
    val stream: KStream[String, String] = builder.stream[String, String]("the_stream")(Consumed.`with`(Serdes.String, Serdes.String))

    // Transformation du flux en mettant à jour les valeurs
    val updatedStream = stream.mapValues { value =>
      // Désérialisation des données IoT et mise à jour de l'alerte si nécessaire
      IoTDataJson.deserialize(value) match {
        case Right(data) => IoTDataJson.serialize(updateAlerte(data)) // Sérialisation des données mises à jour
        case Left(error) =>
          println(s"Deserialization error: ${error.getMessage}") // Affichage de l'erreur de désérialisation
          value // Retourne la valeur d'origine en cas d'erreur
      }
    }

    // Écriture du flux transformé vers un nouveau topic "the_second_stream"
    updatedStream.to("the_second_stream")(Produced.`with`(Serdes.String, Serdes.String))

    // Création et démarrage de l'instance KafkaStreams
    val streams = new KafkaStreams(builder.build(), props)
    streams.start()
    // Ajout d'un hook pour fermer proprement le flux lors de l'arrêt de l'application
    sys.ShutdownHookThread {
      streams.close()
    }
    streams
  }

  // Fonction pour mettre à jour le statut d'alerte dans les données IoT
  def updateAlerte(iotData: IoTData): IoTData = {
    val co2Alert = iotData.qualiteAir.CO2 >= 300000000 // Vérification du seuil de CO2
    val particulesFinesAlert = iotData.qualiteAir.particulesFines >= 100 // Vérification du seuil de particules fines
    // Mise à jour du statut d'alerte si l'un des seuils est dépassé
    if (co2Alert || particulesFinesAlert) {
      iotData.copy(alerte = "Yes") // Copier l'objet IoTData avec le statut d'alerte mis à jour
    } else {
      iotData // Retourner l'objet IoTData sans modification
    }
  }
}