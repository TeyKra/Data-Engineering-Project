// Définition de l'objet singleton IoTDataCsv pour la sérialisation et la désérialisation des données IoT en CSV
object IoTDataCsv {
  // Définition de l'en-tête du fichier CSV, listant les champs des objets IoTData
  val header = "timestamp,deviceId,latitude,longitude,CO2,particulesFines,niveauxSonores,temperature,humidite"

  // Méthode pour sérialiser une séquence d'objets IoTData en une chaîne de caractères formatée en CSV
  def serializeToCsv(data: Seq[IoTData]): String = {
    val rows = data.map { d =>
      s"${d.timestamp},${d.deviceId},${d.location.latitude},${d.location.longitude},${d.qualiteAir.CO2},${d.qualiteAir.particulesFines},${d.niveauxSonores},${d.temperature},${d.humidite}"
    }.mkString("\n")

    header + "\n" + rows
  }

  // Méthode pour désérialiser une chaîne de caractères formatée en CSV en une séquence d'objets IoTData
  def deserializeFromCsv(csv: String): Seq[IoTData] = {
    // Sépare les lignes du CSV et supprime la ligne d'en-tête
    val rows = csv.split("\n").tail

    // Transforme chaque ligne en un objet IoTData
    rows.map { row =>
      val cols = row.split(",")
      IoTData(
        timestamp = cols(0),
        deviceId = cols(1),
        location = Location(cols(2).toDouble, cols(3).toDouble),
        qualiteAir = AirQuality(cols(4).toDouble, cols(5).toDouble),
        niveauxSonores = cols(6).toDouble,
        temperature = cols(7).toDouble,
        humidite = cols(8).toDouble
      )
    }
  }
}