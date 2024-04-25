import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import scala.util.Random
import scala.concurrent.duration._

object Main extends App {
  println("Rapports des données IoT :")
  println("=" * 75)  // Affiche une ligne de séparation

  // Lancement de la simulation en continue
  val start = LocalDateTime.now() // Heure de début

  while (true) {  // Boucle infinie pour une exécution continue
    val currentTime = LocalDateTime.now()
    Capitales.localisations.zipWithIndex.foreach { case (loc, index) =>
      val deviceId = s"device${100 + index}" // ID unique pour chaque dispositif
      val rapport = SimulateurIoT.simulerRapportIoT(deviceId, currentTime, loc)

      // Affichage et traitement du rapport
      print("------------Serialisation JSON------------")
      val json = IoTDataJson.serialize(rapport)
      println(s"\nJSON:\n$json")

      val deserializedData = IoTDataJson.deserialize(json)
      deserializedData match {
        case Right(data) =>
          println("\n")
          println("------------Désérialisation JSON------------")
          println(s"Rapport IoT pour le dispositif ${data.deviceId} à l'instant ${data.timestamp}")
          println(s"Localisation : ${data.location.capital}, Latitude ${data.location.latitude}°, Longitude ${data.location.longitude}°")
          println(s"Qualité de l'Air : CO2 ${data.qualiteAir.CO2} ppm, Particules fines ${data.qualiteAir.particulesFines} µg/m³")
          println(s"Niveaux Sonores : ${data.niveauxSonores} dB")
          println(s"Température : ${data.temperature}°C")
          println(s"Humidité : ${data.humidite}%")
        case Left(error) =>
          println(s"Échec de la désérialisation: ${error.getMessage}")
      }

      println("\n------------Serialisation CSV------------")
      val csv = IoTDataCsv.serializeToCsv(Seq(rapport))
      println(s"\nCSV:\n$csv")

      println("\n------------Désérialisation CSV------------")
      val deserializedCsvData = IoTDataCsv.deserializeFromCsv(csv)
      deserializedCsvData.foreach { data =>
        println(s"Rapport IoT pour le dispositif ${data.deviceId} à l'instant ${data.timestamp}")
        println(s"Localisation : ${data.location.capital}, Latitude ${data.location.latitude}°, Longitude ${data.location.longitude}°")
        println(s"Qualité de l'Air : CO2 ${data.qualiteAir.CO2} ppm, Particules fines ${data.qualiteAir.particulesFines} µg/m³")
        println(s"Niveaux Sonores : ${data.niveauxSonores} dB")
        println(s"Température : ${data.temperature}°C")
        println(s"Humidité : ${data.humidite}%")
      }

      println("-" * 100) // Ligne de séparation entre les rapports
    }
    Thread.sleep(600000) // Pause pour 10 minutes (600000 millisecondes)
  }
}
