import org.apache.kafka.streams.scala.StreamsBuilder
import org.apache.kafka.streams.scala.kstream.{KStream, Consumed, Produced}
import org.apache.kafka.streams.scala.ImplicitConversions._
import org.apache.kafka.common.serialization.Serdes
import org.apache.kafka.streams.{KafkaStreams, StreamsConfig}
import java.util.Properties

object KafkaAlert {
  // Fonction pour démarrer le flux Kafka pour détecter les alertes
  def startAlertStream(): KafkaStreams = {
    // Configuration des propriétés pour Kafka Streams
    val props = new Properties()
    props.put(StreamsConfig.APPLICATION_ID_CONFIG, "iot-alert-emailer") // Identifiant de l'application
    props.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092") // Adresse du serveur Kafka
    props.put(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.String.getClass.getName) // Sérialiseur pour les clés
    props.put(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, Serdes.String.getClass.getName) // Sérialiseur pour les valeurs

    // Création d'un StreamsBuilder pour construire la topologie de traitement de flux
    val builder = new StreamsBuilder()
    // Création d'un flux (stream) à partir du topic "the_second_stream"
    val inputStream: KStream[String, String] = builder.stream[String, String]("the_second_stream")(Consumed.`with`(Serdes.String, Serdes.String))

    // Traitement du flux pour filtrer les messages avec alerte
    val alertStream = inputStream
      .mapValues(value => IoTDataJson.deserialize(value)) // Désérialisation des valeurs JSON
      .filter((_, data) => data.fold(_ => false, _.alerte == "Yes")) // Filtrage des messages avec alerte

    // Transformation des alertes en JSON et envoi vers un nouveau topic "the_third_stream"
    alertStream.mapValues(_.fold(error => "", data => IoTDataJson.serialize(data)))
      .to("the_third_stream")(Produced.`with`(Serdes.String, Serdes.String))

    // Création et démarrage de l'instance KafkaStreams
    val streams = new KafkaStreams(builder.build(), props)
    streams.start()
    // Ajout d'un hook pour fermer proprement le flux lors de l'arrêt de l'application
    sys.ShutdownHookThread {
      streams.close()
    }
    streams
  }
}