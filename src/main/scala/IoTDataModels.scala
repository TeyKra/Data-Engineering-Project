// Import des bibliothèques nécessaires pour l'utilisation de fonctions aléatoires et la manipulation de dates
import scala.util.Random
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

// Définition d'une classe case pour représenter une localisation avec latitude et longitude
case class Location(latitude: Double, longitude: Double)

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

// Objet singleton pour simuler la génération de données IoT
object SimulateurIoT {
  val random = new Random()  // Générateur de nombres aléatoires
  val dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")  // Formatteur de date pour l'horodatage

  // Fonction pour générer aléatoirement une localisation avec des coordonnées dans les plages valides
  def genererLocalisation(): Location = Location(random.between(-90.0, 90.0), random.between(-180.0, 180.0))

  // Fonction pour simuler la génération de rapports IoT sur une période donnée avec un intervalle spécifié
  def simulerRapportIoT(deviceId: String, start: LocalDateTime, end: LocalDateTime, intervalSeconds: Int): Seq[IoTData] = {
    Iterator.iterate(start)(_.plusSeconds(intervalSeconds))  // Crée un itérateur de moments depuis `start` à intervalles réguliers
      .takeWhile(!_.isAfter(end))  // Continue tant que le moment actuel ne dépasse pas `end`
      .map { currentTime =>        // Transforme chaque moment en un objet `IoTData`
        IoTData(
          timestamp = currentTime.format(dateTimeFormatter), // Formatte l'horodatage
          deviceId = deviceId,                               // Utilise l'identifiant de dispositif donné
          location = genererLocalisation(),                  // Génère une localisation
          qualiteAir = AirQuality(random.between(400.0, 5000.0), random.between(0.0, 500.0)),  // Génère des valeurs pour la qualité de l'air
          niveauxSonores = random.between(30.0, 130.0),  // Génère un niveau sonore
          temperature = random.between(-10.0, 40.0),     // Génère une température
          humidite = random.between(0.0, 100.0)          // Génère une humidité
        )
      }.toSeq  // Convertit l'itérateur en une séquence pour faciliter le traitement ultérieur
  }
}
