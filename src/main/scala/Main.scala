import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import scala.util.Random
import scala.concurrent.duration._
import org.apache.spark.sql.{SparkSession, DataFrame} // Ajout de l'import pour DataFrame

object Main extends App {
  println("Rapports des données IoT :")
  println("=" * 75)  // Affiche une ligne de séparation

  // Initialiser une session Spark
  val spark = SparkSession.builder
    .appName("S3ToData")
    .master("local[*]")
    .getOrCreate()

  import spark.implicits._
  // Initialiser un DataFrame vide pour IoTData
  var iotDataFrame: DataFrame = spark.emptyDataset[IoTData].toDF()

  // Démarrer les traitements de streams Kafka
  KafkaStreamProcessing.startStream() // Démarrer le traitement du stream Kafka
  KafkaAlert.startAlertStream()       // Démarrer le stream des alertes
  KafkaSmsSender.startSmsSenderStream()
  DataToS3.startStream()

  // Ajouter un hook pour fermer les ressources lors de la fermeture de l'application
  sys.addShutdownHook {
    MyKafkaProducer.close()
    DataToS3.s3Client.close()
    println("Fermeture du producteur Kafka.")
  }

  // Lancement de la simulation en continu
  val start = LocalDateTime.now() // Heure de début

  while (true) {  // Boucle infinie pour une exécution continue
    val currentTime = LocalDateTime.now()
    Capitales.localisations.zipWithIndex.foreach { case (loc, index) =>
      val deviceId = s"device${100 + index}" // ID unique pour chaque dispositif
      val alerte = "No"
      val rapport = SimulateurIoT.simulerRapportIoT(deviceId, currentTime, loc, alerte)

      println("------------Serialisation JSON------------")
      val json = IoTDataJson.serialize(rapport)
      println(s"\nJSON:\n$json")

      try {
        MyKafkaProducer.sendIoTData("the_stream", rapport.deviceId, json)
      } catch {
        case e: Exception => println(s"Erreur lors de l'envoi à Kafka: ${e.getMessage}")
      }

      val deserializedData = IoTDataJson.deserialize(json)
      deserializedData match {
        case Right(data) =>
          println("\n------------Désérialisation JSON------------")
          println(s"Rapport IoT pour le dispositif ${data.deviceId} à l'instant ${data.timestamp}")
          println(s"Localisation : ${data.location.capital}, Latitude ${data.location.latitude}°, Longitude ${data.location.longitude}°")
          println(s"Qualité de l'Air : CO2 ${data.qualiteAir.CO2} ppm, Particules fines ${data.qualiteAir.particulesFines} µg/m³")
          println(s"Niveaux Sonores : ${data.niveauxSonores} dB")
          println(s"Température : ${data.temperature}°C")
          println(s"Humidité : ${data.humidite}%")
          println(s"Alerte : ${data.alerte}")
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
        println(s"Alerte : ${data.alerte}")
      }

      println("-" * 100) // Ligne de séparation entre les rapports
    }

    // Charger les nouvelles données depuis S3 et les ajouter au DataFrame
    iotDataFrame = S3ToData.loadNewDataToDataFrame(spark, iotDataFrame)
    iotDataFrame.show(false) // Afficher les données pour vérifier

    Thread.sleep(150000) // Pause pour 2'30 minutes
  }
}