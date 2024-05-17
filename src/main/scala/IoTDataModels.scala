import scala.util.Random
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

case class Location(capital: String, latitude: Double, longitude: Double)
case class AirQuality(CO2: Double, particulesFines: Double)
case class IoTData(
    timestamp: String,
    deviceId: String,
    location: Location,
    qualiteAir: AirQuality,
    niveauxSonores: Double,
    temperature: Double,
    humidite: Double,
    alerte: String
)

object Capitales {
  val localisations = Seq(
    Location("Paris", 48.8566, 2.3522),
    Location("Londres", 51.5074, -0.1278),
    Location("Tokyo", 35.6895, 139.6917),
    Location("New York", 40.7128, -74.0060),
    Location("Moscou", 55.7558, 37.6173),
    Location("Pékin", 39.9042, 116.4074),
    Location("Sydney", -33.8688, 151.2093),
    Location("Berlin", 52.5200, 13.4050),
    Location("Mexico", 19.4326, -99.1332),
    Location("Los Angeles", 34.0522, -118.2437)
  )
}

object SimulateurIoT {
  val random = new Random()
  val dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")

  def genererLocalisation(): Location = Capitales.localisations(random.nextInt(Capitales.localisations.length))

  def simulerRapportIoT(deviceId: String, currentTime: LocalDateTime, location: Location, alerte: String): IoTData = {
    val qualiteAir = if (random.nextDouble() < 0.2) {
      // 20% de probabilité d'alerte
      AirQuality(
        CO2 = 300000000 + random.nextDouble() * 1000,  // Générer des valeurs de CO2 au-dessus du seuil
        particulesFines = 100 + random.nextDouble() * 50 // Générer des valeurs de particules fines au-dessus du seuil
      )
    } else {
      // 80% de probabilité de non-alerte
      AirQuality(
        CO2 = 0 + random.nextDouble() * 299999999, // Générer des valeurs de CO2 en dessous du seuil
        particulesFines = 0 + random.nextDouble() * 99 // Générer des valeurs de particules fines en dessous du seuil
      )
    }

    IoTData(
      timestamp = currentTime.format(dateTimeFormatter),
      deviceId = deviceId,
      location = location,
      qualiteAir = qualiteAir,
      niveauxSonores = random.nextDouble() * (130.0 - 30.0) + 30.0,
      temperature = random.nextDouble() * (40.0 + 10.0) - 10.0,
      humidite = random.nextDouble() * 100.0,
      alerte = alerte // Utiliser la valeur d'alerte fournie
    )
  }
}