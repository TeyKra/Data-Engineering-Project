object IoTDataCsv {
  val header = "timestamp,deviceId,capital,latitude,longitude,CO2,particulesFines,niveauxSonores,temperature,humidite,alerte"

  def serializeToCsv(data: Seq[IoTData]): String = {
    val rows = data.map { d =>
      s"${d.timestamp},${d.deviceId},${d.location.capital},${d.location.latitude},${d.location.longitude},${d.qualiteAir.CO2},${d.qualiteAir.particulesFines},${d.niveauxSonores},${d.temperature},${d.humidite},${d.alerte}"
    }.mkString("\n")

    header + "\n" + rows
  }

  def deserializeFromCsv(csv: String): Seq[IoTData] = {
    val rows = csv.split("\n").tail
    rows.map { row =>
      val cols = row.split(",")
      IoTData(
        timestamp = cols(0),
        deviceId = cols(1),
        location = Location(cols(2), cols(3).toDouble, cols(4).toDouble),
        qualiteAir = AirQuality(cols(5).toDouble, cols(6).toDouble),
        niveauxSonores = cols(7).toDouble,
        temperature = cols(8).toDouble,
        humidite = cols(9).toDouble,
        alerte = if (cols.length > 10) cols(10) else "No"  // Gérer le cas où le champ n'est pas présent
      )
    }
  }
}
