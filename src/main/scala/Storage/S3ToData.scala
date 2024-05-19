import org.apache.spark.sql.{SparkSession, DataFrame}
import software.amazon.awssdk.services.s3.S3Client
import software.amazon.awssdk.services.s3.model.{ListObjectsV2Request, GetObjectRequest}
import java.nio.file.{Paths, Files}
import java.time.Instant
import java.util.Properties
import scala.collection.JavaConverters._
import io.circe.parser._
import io.circe.generic.auto._

object S3ToData {
  val bucketName = "efreidataengineering"
  val s3Client: S3Client = S3Client.builder().build()

  var lastCheckTime: Instant = Instant.now()

  def getNewS3Objects(): Seq[String] = {
    val listObjectsReq = ListObjectsV2Request.builder()
      .bucket(bucketName)
      .build()

    val listObjectsResp = s3Client.listObjectsV2(listObjectsReq)

    val newObjects = listObjectsResp.contents().asScala.filter { obj =>
      obj.lastModified().isAfter(lastCheckTime)
    }

    // Mettre à jour le temps du dernier check
    lastCheckTime = Instant.now()

    newObjects.map(_.key()).toSeq
  }

  def downloadObject(key: String): String = {
    val getObjectReq = GetObjectRequest.builder()
      .bucket(bucketName)
      .key(key)
      .build()

    val s3Object = s3Client.getObject(getObjectReq)

    val localFilePath = Paths.get(s"/tmp/$key")
    Files.copy(s3Object, localFilePath)

    new String(Files.readAllBytes(localFilePath))
  }

  def deserializeJson(jsonString: String): IoTData = {
    decode[IoTData](jsonString) match {
      case Right(data) => data
      case Left(error) => throw new RuntimeException(s"Failed to deserialize JSON: ${error.getMessage}")
    }
  }

  def loadNewDataToDataFrame(spark: SparkSession, df: DataFrame): DataFrame = {
    val newKeys = getNewS3Objects()

    val newData = newKeys.map { key =>
      val jsonData = downloadObject(key)
      deserializeJson(jsonData)
    }

    import spark.implicits._
    val newDf = spark.createDataset(newData).toDF()

    df.union(newDf)
  }
}

