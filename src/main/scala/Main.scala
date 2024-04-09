// Import des classes nécessaires pour manipuler des dates et du temps ainsi que pour utiliser des fonctions aléatoires
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import scala.util.Random

// Définition d'un objet singleton `Main` qui étend `App`, permettant ainsi une exécution directe du code contenu dans son corps
object Main extends App {
  // Affichage initial indiquant le début du rapport des données IoT
  println("Rapports des données IoT :")
  println("=" * 75) // Affiche une ligne de 75 signes égal pour marquer visuellement une séparation

  // Définition de la période de simulation
  val start = LocalDateTime.now() // Heure de début de la simulation
  val end = start.plusMinutes(5) // Heure de fin, ici 5 minutes après le début

  // Génération des rapports de données IoT pour un dispositif spécifique toutes les 60 secondes
  val rapports = SimulateurIoT.simulerRapportIoT("device123", start, end, 60)

  // Boucle sur chaque rapport généré pour afficher ses détails
  rapports.foreach { rapport =>
    // Affichage des données en formats JSON et CSV
    print("------------Serialisation JSON------------")
    val json = IoTDataJson.serialize(rapport)
    println(s"\nJSON:\n$json") // Affichage du rapport en format JSON
    // Désérialisation du JSON pour récupérer un objet IoTData
    val deserializedData = IoTDataJson.deserialize(json)
    deserializedData match {
      case Right(data) =>
        // Formatage et affichage des données désérialisées similairement à l'affichage initial
        println("\n")
        println("------------Désérialisation JSON------------")
        println(s"Rapport IoT pour le dispositif ${data.deviceId} à l'instant ${data.timestamp}")
        println(s"Localisation : Latitude ${data.location.latitude}°, Longitude ${data.location.longitude}°")
        println(s"Qualité de l'Air : CO2 ${data.qualiteAir.CO2} ppm, Particules fines ${data.qualiteAir.particulesFines} µg/m³")
        println(s"Niveaux Sonores : ${data.niveauxSonores} dB")
        println(s"Température : ${data.temperature}°C")
        println(s"Humidité : ${data.humidite}%")
      case Left(error) =>
        println(s"Échec de la désérialisation: ${error.getMessage}")
    }
    
    println("\n------------Serialisation CSV------------")
    val csv = IoTDataCsv.serializeToCsv(Seq(rapport))
    println(s"\nCSV:\n$csv") // Affichage du rapport en format CSV

    println("\n------------Désérialisation CSV------------")
    val deserializedCsvData = IoTDataCsv.deserializeFromCsv(csv)
    deserializedCsvData.foreach { data =>
      println(s"Rapport IoT pour le dispositif ${data.deviceId} à l'instant ${data.timestamp}")
      println(s"Localisation : Latitude ${data.location.latitude}°, Longitude ${data.location.longitude}°")
      println(s"Qualité de l'Air : CO2 ${data.qualiteAir.CO2} ppm, Particules fines ${data.qualiteAir.particulesFines} µg/m³")
      println(s"Niveaux Sonores : ${data.niveauxSonores} dB")
      println(s"Température : ${data.temperature}°C")
      println(s"Humidité : ${data.humidite}%")
    }

    println("-" * 100) // Ligne de séparation entre les rapports pour une meilleure lisibilité
  }
}