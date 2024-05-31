// Import the necessary modules from the Circe library for JSON processing
import io.circe.{Encoder, Decoder}
import io.circe.generic.semiauto.{deriveEncoder, deriveDecoder}
import io.circe.parser._
import io.circe.syntax._

// Definition of the `IoTDataJson` singleton object
object IoTDataJson {
  // Implicit definition of the encoder for the `Location` class using the `deriveEncoder` method which generates an encoder automatically
  implicit val locationEncoder: Encoder[Location] = deriveEncoder
  // Implicit decoder definition for the `Location` class using the `deriveDecoder` method to generate a decoder
  implicit val locationDecoder: Decoder[Location] = deriveDecoder

  // Implicit definition of the encoder for the `AirQuality` class
  implicit val airQualityEncoder: Encoder[AirQuality] = deriveEncoder
  // Implicit definition of the decoder for the `AirQuality` class
  implicit val airQualityDecoder: Decoder[AirQuality] = deriveDecoder

  // Implicit definition of the encoder for the `IoTData` class
  implicit val ioTDataEncoder: Encoder[IoTData] = deriveEncoder
  // Implicit definition of the decoder for the `IoTData` class
  implicit val ioTDataDecoder: Decoder[IoTData] = deriveDecoder

  // Method to serialize an instance of `IoTData` into a JSON string
  def serialize(iotData: IoTData): String = iotData.asJson.noSpaces
  // Method to deserialize a JSON string into an instance of `IoTData`, returns an Either containing an error or the object deserialized
  def deserialize(jsonString: String): Either[io.circe.Error, IoTData] = decode[IoTData](jsonString)
}