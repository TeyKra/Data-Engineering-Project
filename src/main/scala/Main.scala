import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import scala.util.{Random, Try, Success, Failure}
import scala.concurrent.duration._
import org.apache.spark.sql.{SparkSession, DataFrame} // Ajout de l'import pour DataFrame

object Main extends App {
  println("Rapports des données IoT :")
  println("=" * 75)  // Affiche une ligne de séparation

  // Initialiser une session Spark avec la configuration S3
  val spark = SparkSession.builder
    .appName("S3ToData")
    .master("local[*]") // Utilisation de tous les cœurs disponibles localement
    .config("spark.hadoop.fs.s3a.impl", "org.apache.hadoop.fs.s3a.S3AFileSystem")
    .config("spark.hadoop.fs.s3a.aws.credentials.provider", "com.amazonaws.auth.DefaultAWSCredentialsProviderChain")
    .config("spark.hadoop.fs.s3a.endpoint", "s3.amazonaws.com")
    .getOrCreate()

  import spark.implicits._
  // Initialiser un DataFrame vide pour IoTData
  val initialIoTDataFrame: DataFrame = spark.emptyDataset[IoTData].toDF()
  val initialProcessedFiles = Set[String]()  // Set pour garder une trace des fichiers traités

  // Démarrer les traitements de streams Kafka
  KafkaStreamProcessing.startStream() // Démarrer le traitement du stream Kafka
  KafkaAlert.startAlertStream()       // Démarrer le stream des alertes
  KafkaSmsSender.startSmsSenderStream() // Démarrer le stream pour l'envoi de SMS
  DataToS3.startStream()              // Démarrer le stream pour l'upload vers S3

  // Ajouter un hook pour fermer les ressources lors de la fermeture de l'application
  sys.addShutdownHook {
    MyKafkaProducer.close()
    DataToS3.s3Client.close()
    println("Fermeture du producteur Kafka.")
  }

  // Lancement de la simulation en continu
  val start = LocalDateTime.now() // Heure de début

  // Fonction pour lancer la simulation en continu
  def runSimulation(currentIoTDataFrame: DataFrame, currentProcessedFiles: Set[String]): Unit = {
    val currentTime = LocalDateTime.now() // Heure actuelle
    // Pour chaque localisation et son index
    Capitales.localisations.zipWithIndex.foreach { case (loc, index) =>
      val deviceId = s"device${100 + index}" // ID unique pour chaque dispositif
      val alerte = "No" // Initialisation de l'alerte à "No"
      val rapport = SimulateurIoT.simulerRapportIoT(deviceId, currentTime, loc, alerte) // Génération du rapport

      println("------------Serialisation JSON------------")
      val json = IoTDataJson.serialize(rapport)
      println(s"\nJSON:\n$json")

      Try {
        MyKafkaProducer.sendIoTData("the_stream", rapport.deviceId, json) // Envoi des données IoT à Kafka
      } match {
        case Success(_) => // Do nothing or log success
        case Failure(e) => println(s"Erreur lors de l'envoi à Kafka: ${e.getMessage}")
      }

      val deserializedData = IoTDataJson.deserialize(json) // Désérialisation des données JSON
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
    val (updatedDataFrame, updatedProcessedFiles) = S3ToData.loadNewDataToDataFrame(spark, currentIoTDataFrame, currentProcessedFiles)

    updatedDataFrame.show(false) // Afficher les données pour vérifier

    Thread.sleep(6000) // Pause pour 1 minute

    runSimulation(updatedDataFrame, updatedProcessedFiles) // Appel récursif avec les nouvelles valeurs
  }

  // Démarrer la simulation
  runSimulation(initialIoTDataFrame, initialProcessedFiles)
}