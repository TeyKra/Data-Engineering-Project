// Import des modules nécessaires de la bibliothèque Circe pour le traitement JSON
import io.circe.{Encoder, Decoder}
import io.circe.generic.semiauto.{deriveEncoder, deriveDecoder}
import io.circe.parser._
import io.circe.syntax._

// Définition de l'objet singleton `IoTDataJson`
object IoTDataJson {
  // Définition implicite de l'encodeur pour la classe `Location` utilisant la méthode `deriveEncoder` qui génère un encodeur automatiquement
  implicit val locationEncoder: Encoder[Location] = deriveEncoder
  // Définition implicite du décodeur pour la classe `Location` utilisant la méthode `deriveDecoder` pour générer un décodeur
  implicit val locationDecoder: Decoder[Location] = deriveDecoder

  // Définition implicite de l'encodeur pour la classe `AirQuality`
  implicit val airQualityEncoder: Encoder[AirQuality] = deriveEncoder
  // Définition implicite du décodeur pour la classe `AirQuality`
  implicit val airQualityDecoder: Decoder[AirQuality] = deriveDecoder

  // Définition implicite de l'encodeur pour la classe `IoTData`
  implicit val ioTDataEncoder: Encoder[IoTData] = deriveEncoder
  // Définition implicite du décodeur pour la classe `IoTData`
  implicit val ioTDataDecoder: Decoder[IoTData] = deriveDecoder

  // Méthode pour sérialiser une instance de `IoTData` en chaîne JSON
  def serialize(iotData: IoTData): String = iotData.asJson.noSpaces
  // Méthode pour désérialiser une chaîne JSON en une instance de `IoTData`, renvoie un Either contenant une erreur ou l'objet désérialisé
  def deserialize(jsonString: String): Either[io.circe.Error, IoTData] = decode[IoTData](jsonString)
}