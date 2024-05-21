object IoTDataCsv {
  // Définition de l'en-tête pour les données CSV
  val header = "timestamp,deviceId,capital,latitude,longitude,CO2,particulesFines,niveauxSonores,temperature,humidite,alerte"

  // Fonction pour sérialiser une séquence de données IoT en format CSV
  def serializeToCsv(data: Seq[IoTData]): String = {
    // Transformation de chaque objet IoTData en une ligne CSV
    val rows = data.map { d =>
      s"${d.timestamp},${d.deviceId},${d.location.capital},${d.location.latitude},${d.location.longitude},${d.qualiteAir.CO2},${d.qualiteAir.particulesFines},${d.niveauxSonores},${d.temperature},${d.humidite},${d.alerte}"
    }.mkString("\n")  // Jointure des lignes avec des sauts de ligne

    // Retourne l'en-tête suivi des lignes de données
    header + "\n" + rows
  }

  // Fonction pour désérialiser des données CSV en une séquence d'objets IoTData
  def deserializeFromCsv(csv: String): Seq[IoTData] = {
    // Séparation des lignes CSV, en ignorant l'en-tête
    val rows = csv.split("\n").tail
    // Transformation de chaque ligne CSV en un objet IoTData
    rows.map { row =>
      val cols = row.split(",")  // Séparation des colonnes par des virgules
      IoTData(
        timestamp = cols(0),  // Premier élément : timestamp
        deviceId = cols(1),   // Deuxième élément : deviceId
        // Troisième, quatrième et cinquième éléments : informations de localisation
        location = Location(cols(2), cols(3).toDouble, cols(4).toDouble),
        // Sixième et septième éléments : qualité de l'air
        qualiteAir = AirQuality(cols(5).toDouble, cols(6).toDouble),
        niveauxSonores = cols(7).toDouble,  // Huitième élément : niveaux sonores
        temperature = cols(8).toDouble,     // Neuvième élément : température
        humidite = cols(9).toDouble,        // Dixième élément : humidité
        // Onzième élément (optionnel) : alerte, avec une valeur par défaut si non présent
        alerte = if (cols.length > 10) cols(10) else "No"
      )
    }
  }
}