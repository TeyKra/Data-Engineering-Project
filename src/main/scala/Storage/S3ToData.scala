import org.apache.spark.sql.{SparkSession, DataFrame}
import org.apache.spark.sql.functions._
import org.apache.spark.sql.types._
import software.amazon.awssdk.services.s3.S3Client
import software.amazon.awssdk.services.s3.model.ListObjectsV2Request
import software.amazon.awssdk.regions.Region
import software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider

import scala.jdk.CollectionConverters._
import java.nio.file.{Files, Paths}
import org.apache.pdfbox.pdmodel.{PDDocument, PDPage}
import org.apache.pdfbox.pdmodel.PDPageContentStream
import org.apache.pdfbox.pdmodel.font.PDType1Font

object S3ToData {
  // Création d'un client S3 avec les paramètres de région et les informations d'identification par défaut
  val s3Client: S3Client = S3Client.builder()
    .region(Region.EU_NORTH_1)
    .credentialsProvider(DefaultCredentialsProvider.create())
    .build()

  val bucketName = "efreidataengineering" // Nom du bucket S3

  // Définition du schéma des données IoT
  val iotDataSchema = StructType(Seq(
    StructField("timestamp", StringType, true),
    StructField("deviceId", StringType, true),
    StructField("location", StructType(Seq(
      StructField("capital", StringType, true),
      StructField("latitude", DoubleType, true),
      StructField("longitude", DoubleType, true)
    )), true),
    StructField("qualiteAir", StructType(Seq(
      StructField("CO2", DoubleType, true),
      StructField("particulesFines", DoubleType, true)
    )), true),
    StructField("niveauxSonores", DoubleType, true),
    StructField("temperature", DoubleType, true),
    StructField("humidite", DoubleType, true),
    StructField("alerte", StringType, true)
  ))

  // Fonction pour charger les nouvelles données dans un DataFrame Spark et mettre à jour les fichiers traités
  def loadNewDataToDataFrame(spark: SparkSession, iotDataFrame: DataFrame, processedFiles: Set[String]): (DataFrame, Set[String]) = {
    import spark.implicits._

    val (newFiles, updatedProcessedFiles) = listNewFiles(processedFiles) // Liste des nouveaux fichiers

    if (newFiles.nonEmpty) {
      // Lecture des nouveaux fichiers JSON avec le schéma défini
      val newDF = spark.read.schema(iotDataSchema).json(newFiles: _*)

      // Filtrer les lignes déjà présentes dans le DataFrame existant
      val existingData = iotDataFrame.select("deviceId", "timestamp").as[(String, String)].collect().toSet
      val newUniqueDF = newDF.filter(row => !existingData.contains((row.getAs[String]("deviceId"), row.getAs[String]("timestamp"))))

      // Union du DataFrame existant avec les nouvelles données uniques
      val updatedDF = iotDataFrame.union(newUniqueDF)
        .dropDuplicates("deviceId", "timestamp")
        .orderBy("timestamp", "deviceId")

      generateReport(spark, updatedDF) // Génération du rapport

      (updatedDF, updatedProcessedFiles)
    } else {
      (iotDataFrame, processedFiles) // Retourner le DataFrame inchangé et les fichiers traités
    }
  }

  // Fonction pour lister les nouveaux fichiers dans le bucket S3
  def listNewFiles(processedFiles: Set[String]): (Seq[String], Set[String]) = {
    val listObjectsRequest = ListObjectsV2Request.builder()
      .bucket(bucketName)
      .build()

    val listObjectsResponse = s3Client.listObjectsV2(listObjectsRequest)
    val allFiles = listObjectsResponse.contents().asScala.map(_.key()).filter(_.endsWith(".json")).toSeq

    val newFiles = allFiles.filterNot(processedFiles.contains).map(file => s"s3a://$bucketName/$file")
    val updatedProcessedFiles = processedFiles ++ newFiles.map(_.split("/").last)

    (newFiles, updatedProcessedFiles)
  }

  // Fonction pour générer un rapport PDF à partir des données IoT
  def generateReport(spark: SparkSession, df: DataFrame): Unit = {
    import spark.implicits._

    // Compter les alertes par capitale et type d'alerte
    val alertCount = df.groupBy("location.capital", "alerte").count()
    // Obtenir les 3 capitales avec les niveaux de CO2 les plus élevés
    val topCO2Capitals = df.groupBy("location.capital")
      .agg(max("qualiteAir.CO2").alias("max_CO2"))
      .orderBy(desc("max_CO2"))
      .limit(3)
    // Filtrer les alertes par horodatage
    val alertsByTimestamp = df.filter(col("alerte") === "Yes").select("timestamp", "location.capital", "qualiteAir.CO2", "qualiteAir.particulesFines")

    // Obtenir le dernier horodatage des données
    val latestTimestamp = df.agg(max("timestamp")).as[String].take(1).headOption.getOrElse("unknown")

    // Chemin et nom du fichier de rapport PDF
    val reportPath = "src/main/scala/Report"
    val reportFile = Paths.get(s"$reportPath/report_$latestTimestamp.pdf")
    if (!Files.exists(reportFile.getParent)) {
      Files.createDirectories(reportFile.getParent)
    }

    // Création d'un document PDF avec PDFBox
    val document = new PDDocument()
    val page = new PDPage()
    document.addPage(page)

    val contentStream = new PDPageContentStream(document, page)
    contentStream.setFont(PDType1Font.HELVETICA, 12)

    // Écriture du contenu du rapport PDF
    contentStream.beginText()
    contentStream.newLineAtOffset(50, 750)
    contentStream.showText(s"Report generated at: $latestTimestamp")
    contentStream.newLineAtOffset(0, -20)
    contentStream.showText("Alert Counts by Country:")
    contentStream.newLineAtOffset(0, -20)

    alertCount.collect().foreach { row =>
      contentStream.showText(s"${row.getString(0)} - ${row.getString(1)}: ${row.getLong(2)}")
      contentStream.newLineAtOffset(0, -20)
    }

    contentStream.newLineAtOffset(0, -20)
    contentStream.showText("Top 3 Capitals with Highest CO2 Levels:")
    contentStream.newLineAtOffset(0, -20)

    topCO2Capitals.collect().foreach { row =>
      contentStream.showText(s"${row.getString(0)}: ${row.getDouble(1)} ppm")
      contentStream.newLineAtOffset(0, -20)
    }

    contentStream.newLineAtOffset(0, -20)
    contentStream.showText("Alerts by Timestamp:")
    contentStream.newLineAtOffset(0, -20)

    alertsByTimestamp.collect().foreach { row =>
      contentStream.showText(s"${row.getString(0)} - ${row.getString(1)}: CO2 ${row.getDouble(2)} ppm, Particules Fines ${row.getDouble(3)} µg/m³")
      contentStream.newLineAtOffset(0, -20)
    }

    contentStream.endText()
    contentStream.close()

    // Sauvegarde du document PDF
    document.save(reportFile.toFile)
    document.close()

    println(s"Report saved to: $reportFile")
  }
}