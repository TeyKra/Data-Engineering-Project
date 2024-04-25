// Import des bibliothèques nécessaires pour l'utilisation de fonctions aléatoires et la manipulation de dates
import scala.util.Random
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

// Définition d'une classe case pour représenter une localisation avec latitude et longitude
case class Location(capital: String, latitude: Double, longitude: Double)

// Définition d'une classe case pour représenter la qualité de l'air avec des mesures de CO2 et de particules fines
case class AirQuality(CO2: Double, particulesFines: Double)

// Définition d'une classe case pour les données IoT, contenant toutes les informations nécessaires d'un rapport
case class IoTData(
    timestamp: String,       // Horodatage de la mesure
    deviceId: String,        // Identifiant du dispositif IoT
    location: Location,      // Localisation du dispositif
    qualiteAir: AirQuality,  // Qualité de l'air mesurée
    niveauxSonores: Double,  // Niveaux sonores en décibels
    temperature: Double,     // Température en degrés Celsius
    humidite: Double         // Humidité en pourcentage
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
  val random = new Random()  // Générateur de nombres aléatoires
  val dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")  // Formatteur de date pour l'horodatage

  def genererLocalisation(): Location = Capitales.localisations(random.nextInt(Capitales.localisations.length))

  def simulerRapportIoT(deviceId: String, currentTime: LocalDateTime, location: Location): IoTData = {
    IoTData(
      timestamp = currentTime.format(dateTimeFormatter),
      deviceId = deviceId,
      location = location,
      qualiteAir = AirQuality(random.between(400.0, 5000.0), random.between(0.0, 500.0)),
      niveauxSonores = random.between(30.0, 130.0),
      temperature = random.between(-10.0, 40.0),
      humidite = random.between(0.0, 100.0)
    )
  }
} // Fin de l'objet SimulateurIoT

