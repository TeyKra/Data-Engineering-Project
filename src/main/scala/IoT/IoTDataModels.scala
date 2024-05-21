import scala.util.Random
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

// Définition de la classe Location avec des champs pour la capitale, la latitude et la longitude
case class Location(capital: String, latitude: Double, longitude: Double)

// Définition de la classe AirQuality avec des champs pour le CO2 et les particules fines
case class AirQuality(CO2: Double, particulesFines: Double)

// Définition de la classe IoTData qui regroupe toutes les informations collectées par un appareil IoT
case class IoTData(
    timestamp: String,        // Horodatage du rapport
    deviceId: String,         // Identifiant de l'appareil
    location: Location,       // Localisation de l'appareil
    qualiteAir: AirQuality,   // Qualité de l'air mesurée
    niveauxSonores: Double,   // Niveau sonore mesuré
    temperature: Double,      // Température mesurée
    humidite: Double,         // Humidité mesurée
    alerte: String            // Statut d'alerte
)

// Objet Capitales contenant une liste de localisations prédéfinies pour les capitales du monde
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

// Objet SimulateurIoT pour simuler des rapports de données IoT
object SimulateurIoT {
  val random = new Random()  // Générateur de nombres aléatoires
  val dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")  // Format pour l'horodatage

  // Fonction pour générer une localisation aléatoire à partir des capitales prédéfinies
  def genererLocalisation(): Location = Capitales.localisations(random.nextInt(Capitales.localisations.length))

  // Fonction pour simuler un rapport IoT
  def simulerRapportIoT(deviceId: String, currentTime: LocalDateTime, location: Location, alerte: String): IoTData = {
    // Génération des valeurs de qualité de l'air avec une probabilité d'alerte
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

    // Création d'un objet IoTData avec des valeurs simulées
    IoTData(
      timestamp = currentTime.format(dateTimeFormatter),  // Formatage de l'horodatage
      deviceId = deviceId,                                // Utilisation de l'identifiant de l'appareil fourni
      location = location,                                // Utilisation de la localisation fournie
      qualiteAir = qualiteAir,                            // Utilisation de la qualité de l'air générée
      niveauxSonores = random.nextDouble() * (130.0 - 30.0) + 30.0,  // Génération d'un niveau sonore aléatoire entre 30 et 130 dB
      temperature = random.nextDouble() * (40.0 + 10.0) - 10.0,      // Génération d'une température aléatoire entre -10 et 40 degrés Celsius
      humidite = random.nextDouble() * 100.0,                         // Génération d'une humidité aléatoire entre 0 et 100%
      alerte = alerte // Utilisation de la valeur d'alerte fournie
    )
  }
}