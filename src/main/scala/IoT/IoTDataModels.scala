import scala.util.Random
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

// Definition of the Location class with fields for capital, latitude and longitude
case class Location(capital: String, latitude: Double, longitude: Double)

// Definition of the Location class with fields for capital, latitude and longitude
case class AirQuality(CO2: Double, particulesFines: Double)

// Definition of the IoTData class which groups together all the information collected by an IoT device
case class IoTData(
    timestamp: String,        // Report timestamp
    deviceId: String,         // Device ID
    location: Location,       // Device location
    qualiteAir: AirQuality,   // Air quality measured
    niveauxSonores: Double,   // Measured sound level
    temperature: Double,      // Measured temperature
    humidite: Double,         // Humidity measured
    alerte: String            // Alert status
)

// Capitals object containing a list of predefined locations for the capitals of the world
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

// IoT Simulator object to simulate IoT data reports
object SimulateurIoT {
  val random = new Random()  // Random number generator
  val dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")  // Format for timestamp

  // Function to generate a random location from predefined capitals
  def genererLocalisation(): Location = Capitales.localisations(random.nextInt(Capitales.localisations.length))

  // Function to simulate an IoT report
  def simulerRapportIoT(deviceId: String, currentTime: LocalDateTime, location: Location, alerte: String): IoTData = {
    // Generation of air quality values ​​with an alert probability
    val qualiteAir = if (random.nextDouble() < 0.2) {
      // 20% alert probability
      AirQuality(
        CO2 = 300000000 + random.nextDouble() * 1000,  // Generate CO2 values ​​above threshold
        particulesFines = 100 + random.nextDouble() * 50 // Generate fine particle values ​​above threshold
      )
    } else {
      // 80% probability of non-alert
      AirQuality(
        CO2 = 0 + random.nextDouble() * 299999999, // Generate CO2 values ​​below the threshold
        particulesFines = 0 + random.nextDouble() * 99 // Generate fine particle values ​​below the threshold
      )
    }

    // Creating an IoTData object with simulated values
    IoTData(
      timestamp = currentTime.format(dateTimeFormatter),  // Formatting the timestamp
      deviceId = deviceId,                                // Using the provided device ID
      location = location,                                // Using the location provided
      qualiteAir = qualiteAir,                            // Use of the air quality generated
      niveauxSonores = random.nextDouble() * (130.0 - 30.0) + 30.0,  // Generation of a random sound level between 30 and 130 dB
      temperature = random.nextDouble() * (40.0 + 10.0) - 10.0,      // Generation of a random temperature between -10 and 40 degrees Celsius
      humidite = random.nextDouble() * 100.0,                        // Generation of random humidity between 0 and 100%
      alerte = alerte // Using the provided alert value
    )
  }
}