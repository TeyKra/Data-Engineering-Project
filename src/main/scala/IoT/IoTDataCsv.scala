object IoTDataCsv {
  // Setting the header for CSV data
  val header = "timestamp,deviceId,capital,latitude,longitude,CO2,particulesFines,niveauxSonores,temperature,humidite,alerte"

  // Function to serialize a sequence of IoT data in CSV format
  def serializeToCsv(data: Seq[IoTData]): String = {
    // Transformation of each IoTData object into a CSV line
    val rows = data.map { d =>
      s"${d.timestamp},${d.deviceId},${d.location.capital},${d.location.latitude},${d.location.longitude},${d.qualiteAir.CO2},${d.qualiteAir.particulesFines},${d.niveauxSonores},${d.temperature},${d.humidite},${d.alerte}"
    }.mkString("\n")  // Join lines with line breaks

    // Return the header followed by the data lines
    header + "\n" + rows
  }

  // Function to deserialize CSV data into a sequence of IoTData objects
  def deserializeFromCsv(csv: String): Seq[IoTData] = {
    // Separate CSV lines, ignoring the header
    val rows = csv.split("\n").tail
    // Transform each CSV line into an IoTData object
    rows.map { row =>
      val cols = row.split(",")  // Separating columns with commas
      IoTData(
        timestamp = cols(0),  // First element: timestamp
        deviceId = cols(1),   // Second element: deviceId
        // Third, fourth and fifth elements: location information
        location = Location(cols(2), cols(3).toDouble, cols(4).toDouble),
        // Sixth and seventh elements: air quality
        qualiteAir = AirQuality(cols(5).toDouble, cols(6).toDouble),
        niveauxSonores = cols(7).toDouble,  // Eighth element: sound levels
        temperature = cols(8).toDouble,     // Ninth element: temperature
        humidite = cols(9).toDouble,        // Tenth element: humidity
        // Eleventh element (optional): alert, with a default value if not present
        alerte = if (cols.length > 10) cols(10) else "No"
      )
    }
  }
}