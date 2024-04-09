# Projet Data Engineering

# Requirements

## Situation

Starting with a startup idea of an innovative service based on:

- IoT devices.
- An information system.

IoT devices are essential, as these devices must emit data every few minutes (or seconds). If your startup is successful, there will be millions of these devices. We consider that you’ve managed to make a working prototype, regardless of what your devices are doing.

Every data emit by the devices must contain: timestamps, device id, latitude and longitude, and any other field you find useful.

Your information system must provide:

- An urgent service referred to as "alert".
- A long-term analytics service.

## Project

It's up to you to report and recommend the right architecture.

Based on the preliminary questions, your solution is very likely to include:

- At least one distributed storage.
- At least one distributed stream.
- At least two stream consumers.

## Preliminary Questions

- What technical/business constraints should the data storage component of the program architecture meet to fulfill the requirement described in the "Statistics" paragraph? So what kind of component(s) (listed in the lecture) will the architecture need?
- What business constraint should the architecture meet to fulfill the requirement described in the "Alert" paragraph? Which component to choose?

# Sujet choisi 

## Contexte 

Création d'un service innovant de surveillance environnementale. L'objectif est de déployer des capteurs IoT dans différents environnements (urbains, industriels, naturels) pour collecter des données sur la qualité de l'air, les niveaux sonores, la température, l'humidité, etc. Ces données servent à la fois à alerter en temps réel en cas de dépassement des seuils critiques et à analyser les tendances environnementales à long terme pour la planification urbaine et la sensibilisation écologique.

## Données Collectées par les Capteurs

- Timestamps : Date et heure de la mesure.
- Device ID : Identifiant unique du capteur.
- Latitude et Longitude : Localisation du capteur.
- Qualité de l'Air : Concentrations de CO2, particules fines, etc.
- Niveaux Sonores : Décibels enregistrés.
- Température et Humidité : Mesures climatiques.

## Architecture Système

- Distributed Storage : Utilisation d'une base de données de séries temporelles distribuée (par exemple, InfluxDB) pour stocker efficacement les données émises par les capteurs en temps réel.

- Distributed Stream : Apache Kafka pour gérer les flux de données en temps réel, permettant une ingestion efficace des données des capteurs et leur distribution aux différents services de consommation.

- Stream Consumers :

    - Service d'Alerte : Un microservice dédié à la surveillance des seuils critiques. En cas de dépassement (par exemple, qualité de l'air mauvaise, niveau sonore élevé), le service génère une alerte envoyée aux autorités compétentes ou aux responsables de la zone surveillée.

    - Dashboard et Reporting : Offrir un accès en temps réel et historique aux données collectées par les capteurs IoT à travers un tableau de bord interactif. Ce service fournira des visualisations claires (graphiques, cartes de chaleur, indicateurs clés) pour surveiller l'état de l'environnement surveillé. Il permettra la génération automatique de rapports périodiques qui résument les tendances, les anomalies et les insights environnementaux sur une période donnée (quotidien, hebdomadaire, mensuel).

## Questions Préliminaires Réponses

- Contraintes Techniques et Commerciales pour le Stockage de Données : Le composant de stockage doit gérer efficacement les écritures en temps réel à grande échelle et permettre des requêtes rapides pour les alertes et les analyses. Il doit être hautement disponible, scalable, et offrir une intégrité des données optimale.

- Contrainte Commerciale pour l'Alerte : Le système doit garantir une latence très faible entre la détection d'un seuil critique et l'émission de l'alerte pour une réaction rapide. Le service d'alerte doit être hautement disponible et fiable.


# Questions

## Les question du projet

- Distributed Storage : Utilisation d'une base de données de séries temporelles distribuée (par exemple, InfluxDB) pour stocker efficacement les données émises par les capteurs en temps réel.

- Distributed Stream : Apache Kafka pour gérer les flux de données en temps réel, permettant une ingestion efficace des données des capteurs et leur distribution aux différents services de consommation.

- Stream Consumers :

    - Service d'Alerte : Un microservice dédié à la surveillance des seuils critiques. En cas de dépassement (par exemple, qualité de l'air mauvaise, niveau sonore élevé), le service génère une alerte envoyée aux autorités compétentes ou aux responsables de la zone surveillée.

    - Dashboard et Reporting : Offrir un accès en temps réel et historique aux données collectées par les capteurs IoT à travers un tableau de bord interactif. Ce service fournira des visualisations claires (graphiques, cartes de chaleur, indicateurs clés) pour surveiller l'état de l'environnement surveillé. Il permettra la génération automatique de rapports périodiques qui résument les tendances, les anomalies et les insights environnementaux sur une période donnée (quotidien, hebdomadaire, mensuel).

- Preliminary question
    - Contraintes Techniques et Commerciales pour le Stockage de Données : Le composant de stockage doit gérer efficacement les écritures en temps réel à grande échelle et permettre des requêtes rapides pour les alertes et les analyses. Il doit être hautement disponible, scalable, et offrir une intégrité des données optimale.

    - Contrainte Commerciale pour l'Alerte : Le système doit garantir une latence très faible entre la détection d'un seuil critique et l'émission de l'alerte pour une réaction rapide. Le service d'alerte doit être hautement disponible et fiable.

## Le diagramme d’architecture

- Capteurs IoT déployés dans différents environnements, collectant des données variées comme la qualité de l'air, les niveaux sonores, la température, et l'humidité.

- Apache Kafka pour gérer les flux de données en temps réel émis par les capteurs.
InfluxDB comme base de données de séries temporelles distribuée pour stocker les données des capteurs.

- Service d'Alerte, un microservice réagissant en temps réel pour détecter et notifier les dépassements de seuils critiques.

- Dashboard et Reporting, offrant une visualisation en temps réel et historique des données via un tableau de bord interactif et la génération de rapports périodiques.


## Un simulateur de votre IoT affichant des rapport de votre IoT:

### Simulateur IoT

Le simulateur IoT, `SimulateurIoT`, utilise la génération aléatoire pour créer des données environnementales simulées représentant des rapports de dispositifs IoT. Il génère des rapports à intervalles réguliers entre un moment de début et de fin, simulant ainsi l'émission continue de données par les capteurs. Chaque rapport inclut des informations telles que la localisation géographique, la qualité de l'air (CO2 et particules fines), les niveaux sonores, la température, et l'humidité, avec des valeurs générées aléatoirement. La localisation est déterminée par des coordonnées latitude et longitude générées de manière aléatoire dans des plages valides globales. Ces rapports simulés sont regroupés dans une séquence, permettant une analyse ou une visualisation ultérieure des données générées.

### Code du simulateur IoT

```scala
// class : IoTDataModels.scala

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
```

## La case class du rapport IoT:

```scala
// class : IoTDataModels.scala

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
```

La `case class IoTData` encapsule les données rapportées par un dispositif IoT, structurant les informations environnementales critiques en un seul objet. Elle inclut le moment précis du rapport (`timestamp`), l'identifiant unique du dispositif (`deviceId`), et la localisation géographique (`Location`) où les données ont été recueillies. En outre, `IoTData` détaille les mesures environnementales spécifiques comme la qualité de l'air (`AirQuality`, incluant CO2 et particules fines), les niveaux sonores, la température et l'humidité, fournissant une vue complète des conditions environnementales au moment du rapport.

## Le compagnon object pouvant serialiser/deserialiser en csv et json:

```scala
// class: IotDataCsv.scala

// Définition de l'objet singleton IoTDataCsv pour la sérialisation et la désérialisation des données IoT en CSV
object IoTDataCsv {
  // Définition de l'en-tête du fichier CSV, listant les champs des objets IoTData
  val header = "timestamp,deviceId,latitude,longitude,CO2,particulesFines,niveauxSonores,temperature,humidite"

  // Méthode pour sérialiser une séquence d'objets IoTData en une chaîne de caractères formatée en CSV
  def serializeToCsv(data: Seq[IoTData]): String = {
    val rows = data.map { d =>
      s"${d.timestamp},${d.deviceId},${d.location.latitude},${d.location.longitude},${d.qualiteAir.CO2},${d.qualiteAir.particulesFines},${d.niveauxSonores},${d.temperature},${d.humidite}"
    }.mkString("\n")

    header + "\n" + rows
  }

  // Méthode pour désérialiser une chaîne de caractères formatée en CSV en une séquence d'objets IoTData
  def deserializeFromCsv(csv: String): Seq[IoTData] = {
    // Sépare les lignes du CSV et supprime la ligne d'en-tête
    val rows = csv.split("\n").tail

    // Transforme chaque ligne en un objet IoTData
    rows.map { row =>
      val cols = row.split(",")
      IoTData(
        timestamp = cols(0),
        deviceId = cols(1),
        location = Location(cols(2).toDouble, cols(3).toDouble),
        qualiteAir = AirQuality(cols(4).toDouble, cols(5).toDouble),
        niveauxSonores = cols(6).toDouble,
        temperature = cols(7).toDouble,
        humidite = cols(8).toDouble
      )
    }
  }
}
```
```scala
// class: IoTDataJson.scala

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
```

Les compagnons `IoTDataCsv` et `IoTDataJson` fournissent des fonctionnalités pour sérialiser et désérialiser les données des dispositifs IoT en formats CSV et JSON, respectivement, facilitant l'exportation, le stockage et le partage des données collectées.

### `IoTDataCsv`

Cette classe gère la sérialisation et la désérialisation des données IoT au format CSV.

- **Sérialisation**:
  - Convertit une séquence d'objets `IoTData` en une chaîne de caractères formatée en CSV.
  - Chaque objet `IoTData` est transformé en une ligne CSV, contenant des informations comme le timestamp, l'identifiant du dispositif, la localisation, la qualité de l'air, les niveaux sonores, la température et l'humidité.
  - L'en-tête du fichier CSV, défini par la variable `header`, décrit les colonnes pour faciliter l'interprétation des données.

- **Désérialisation**:
  - Transforme une chaîne de caractères CSV en une séquence d'objets `IoTData`.
  - Chaque ligne du CSV est convertie en un nouvel objet `IoTData`, en reconstruisant chaque attribut à partir des valeurs séparées par des virgules.

### `IoTDataJson`

Cette classe utilise la bibliothèque Circe pour sérialiser et désérialiser les objets `IoTData` en JSON.

- **Sérialisation**:
  - Convertit un objet `IoTData` en une chaîne JSON compacte, en utilisant des encodeurs dérivés automatiquement pour les classes `IoTData`, `Location` et `AirQuality`.
  - La chaîne JSON est formatée sans espaces superflus, rendant le résultat idéal pour le stockage ou la transmission via des API web.

- **Désérialisation**:
  - Convertit une chaîne JSON en un objet `IoTData`.
  - Utilise des décodeurs pour analyser la chaîne JSON et reconstruire un objet `IoTData`, traitant les erreurs potentielles de formatage ou de typage des données.
  - Renvoie un résultat sous forme de `Either`, qui peut être une erreur ou un objet validement désérialisé, offrant ainsi un moyen robuste de gérer les erreurs de désérialisation.

Ensemble, ces objets facilitent la manipulation des données IoT dans des formats largement adoptés et interopérables, permettant une grande flexibilité dans le traitement des données, leur visualisation, et leur analyse au sein de divers outils et plateformes.

## Output du Simulateur IoT

```
Rapports des données IoT :
===========================================================================
------------Serialisation JSON------------
JSON:
{"timestamp":"2024-04-09 21:51:12","deviceId":"device123","location":{"latitude":-30.24373774820088,"longitude":-69.3052791436667},"qualiteAir":{"CO2":1411.8596912703529,"particulesFines":434.47448633401206},"niveauxSonores":46.67654284218111,"temperature":27.425595555548725,"humidite":62.33179053793582}


------------Désérialisation JSON------------
Rapport IoT pour le dispositif device123 à l'instant 2024-04-09 21:51:12
Localisation : Latitude -30.24373774820088°, Longitude -69.3052791436667°
Qualité de l'Air : CO2 1411.8596912703529 ppm, Particules fines 434.47448633401206 µg/m³
Niveaux Sonores : 46.67654284218111 dB
Température : 27.425595555548725°C
Humidité : 62.33179053793582%

------------Serialisation CSV------------

CSV:
timestamp,deviceId,latitude,longitude,CO2,particulesFines,niveauxSonores,temperature,humidite
2024-04-09 21:51:12,device123,-30.24373774820088,-69.3052791436667,1411.8596912703529,434.47448633401206,46.67654284218111,27.425595555548725,62.33179053793582

------------Désérialisation CSV------------
Rapport IoT pour le dispositif device123 à l'instant 2024-04-09 21:51:12
Localisation : Latitude -30.24373774820088°, Longitude -69.3052791436667°
Qualité de l'Air : CO2 1411.8596912703529 ppm, Particules fines 434.47448633401206 µg/m³
Niveaux Sonores : 46.67654284218111 dB
Température : 27.425595555548725°C
Humidité : 62.33179053793582%
----------------------------------------------------------------------------------------------------
------------Serialisation JSON------------
JSON:
{"timestamp":"2024-04-09 21:52:12","deviceId":"device123","location":{"latitude":53.42567154860336,"longitude":82.28134489430875},"qualiteAir":{"CO2":2423.781036744339,"particulesFines":356.5142666758581},"niveauxSonores":47.438945343780134,"temperature":3.5590271871250447,"humidite":44.925740320538566}


------------Désérialisation JSON------------
Rapport IoT pour le dispositif device123 à l'instant 2024-04-09 21:52:12
Localisation : Latitude 53.42567154860336°, Longitude 82.28134489430875°
Qualité de l'Air : CO2 2423.781036744339 ppm, Particules fines 356.5142666758581 µg/m³
Niveaux Sonores : 47.438945343780134 dB
Température : 3.5590271871250447°C
Humidité : 44.925740320538566%

------------Serialisation CSV------------

CSV:
timestamp,deviceId,latitude,longitude,CO2,particulesFines,niveauxSonores,temperature,humidite
2024-04-09 21:52:12,device123,53.42567154860336,82.28134489430875,2423.781036744339,356.5142666758581,47.438945343780134,3.5590271871250447,44.925740320538566

------------Désérialisation CSV------------
Rapport IoT pour le dispositif device123 à l'instant 2024-04-09 21:52:12
Localisation : Latitude 53.42567154860336°, Longitude 82.28134489430875°
Qualité de l'Air : CO2 2423.781036744339 ppm, Particules fines 356.5142666758581 µg/m³
Niveaux Sonores : 47.438945343780134 dB
Température : 3.5590271871250447°C
Humidité : 44.925740320538566%
----------------------------------------------------------------------------------------------------
------------Serialisation JSON------------
JSON:
{"timestamp":"2024-04-09 21:53:12","deviceId":"device123","location":{"latitude":-55.62579624802514,"longitude":8.18444778902176},"qualiteAir":{"CO2":4032.583967405325,"particulesFines":436.1887101343062},"niveauxSonores":61.08112433076664,"temperature":34.65571828595021,"humidite":2.1134751362920645}


------------Désérialisation JSON------------
Rapport IoT pour le dispositif device123 à l'instant 2024-04-09 21:53:12
Localisation : Latitude -55.62579624802514°, Longitude 8.18444778902176°
Qualité de l'Air : CO2 4032.583967405325 ppm, Particules fines 436.1887101343062 µg/m³
Niveaux Sonores : 61.08112433076664 dB
Température : 34.65571828595021°C
Humidité : 2.1134751362920645%

------------Serialisation CSV------------

CSV:
timestamp,deviceId,latitude,longitude,CO2,particulesFines,niveauxSonores,temperature,humidite
2024-04-09 21:53:12,device123,-55.62579624802514,8.18444778902176,4032.583967405325,436.1887101343062,61.08112433076664,34.65571828595021,2.1134751362920645

------------Désérialisation CSV------------
Rapport IoT pour le dispositif device123 à l'instant 2024-04-09 21:53:12
Localisation : Latitude -55.62579624802514°, Longitude 8.18444778902176°
Qualité de l'Air : CO2 4032.583967405325 ppm, Particules fines 436.1887101343062 µg/m³
Niveaux Sonores : 61.08112433076664 dB
Température : 34.65571828595021°C
Humidité : 2.1134751362920645%
----------------------------------------------------------------------------------------------------
------------Serialisation JSON------------
JSON:
{"timestamp":"2024-04-09 21:54:12","deviceId":"device123","location":{"latitude":86.0993330233982,"longitude":120.9875603758303},"qualiteAir":{"CO2":1378.7593557040614,"particulesFines":278.687885723685},"niveauxSonores":43.999005349954984,"temperature":4.431583022072235,"humidite":74.62698920264702}


------------Désérialisation JSON------------
Rapport IoT pour le dispositif device123 à l'instant 2024-04-09 21:54:12
Localisation : Latitude 86.0993330233982°, Longitude 120.9875603758303°
Qualité de l'Air : CO2 1378.7593557040614 ppm, Particules fines 278.687885723685 µg/m³
Niveaux Sonores : 43.999005349954984 dB
Température : 4.431583022072235°C
Humidité : 74.62698920264702%

------------Serialisation CSV------------

CSV:
timestamp,deviceId,latitude,longitude,CO2,particulesFines,niveauxSonores,temperature,humidite
2024-04-09 21:54:12,device123,86.0993330233982,120.9875603758303,1378.7593557040614,278.687885723685,43.999005349954984,4.431583022072235,74.62698920264702

------------Désérialisation CSV------------
Rapport IoT pour le dispositif device123 à l'instant 2024-04-09 21:54:12
Localisation : Latitude 86.0993330233982°, Longitude 120.9875603758303°
Qualité de l'Air : CO2 1378.7593557040614 ppm, Particules fines 278.687885723685 µg/m³
Niveaux Sonores : 43.999005349954984 dB
Température : 4.431583022072235°C
Humidité : 74.62698920264702%
----------------------------------------------------------------------------------------------------
------------Serialisation JSON------------
JSON:
{"timestamp":"2024-04-09 21:55:12","deviceId":"device123","location":{"latitude":89.17149769743324,"longitude":13.752722566164664},"qualiteAir":{"CO2":792.6761832554516,"particulesFines":253.8152936918205},"niveauxSonores":32.788419919282795,"temperature":28.3946839513031,"humidite":52.886460611724104}


------------Désérialisation JSON------------
Rapport IoT pour le dispositif device123 à l'instant 2024-04-09 21:55:12
Localisation : Latitude 89.17149769743324°, Longitude 13.752722566164664°
Qualité de l'Air : CO2 792.6761832554516 ppm, Particules fines 253.8152936918205 µg/m³
Niveaux Sonores : 32.788419919282795 dB
Température : 28.3946839513031°C
Humidité : 52.886460611724104%

------------Serialisation CSV------------

CSV:
timestamp,deviceId,latitude,longitude,CO2,particulesFines,niveauxSonores,temperature,humidite
2024-04-09 21:55:12,device123,89.17149769743324,13.752722566164664,792.6761832554516,253.8152936918205,32.788419919282795,28.3946839513031,52.886460611724104

------------Désérialisation CSV------------
Rapport IoT pour le dispositif device123 à l'instant 2024-04-09 21:55:12
Localisation : Latitude 89.17149769743324°, Longitude 13.752722566164664°
Qualité de l'Air : CO2 792.6761832554516 ppm, Particules fines 253.8152936918205 µg/m³
Niveaux Sonores : 32.788419919282795 dB
Température : 28.3946839513031°C
Humidité : 52.886460611724104%
----------------------------------------------------------------------------------------------------
------------Serialisation JSON------------
JSON:
{"timestamp":"2024-04-09 21:56:12","deviceId":"device123","location":{"latitude":20.843545866189686,"longitude":-41.124178350240726},"qualiteAir":{"CO2":4529.644314049281,"particulesFines":258.6918564139046},"niveauxSonores":82.42913014338637,"temperature":19.220059629322044,"humidite":23.90762112715723}


------------Désérialisation JSON------------
Rapport IoT pour le dispositif device123 à l'instant 2024-04-09 21:56:12
Localisation : Latitude 20.843545866189686°, Longitude -41.124178350240726°
Qualité de l'Air : CO2 4529.644314049281 ppm, Particules fines 258.6918564139046 µg/m³
Niveaux Sonores : 82.42913014338637 dB
Température : 19.220059629322044°C
Humidité : 23.90762112715723%

------------Serialisation CSV------------

CSV:
timestamp,deviceId,latitude,longitude,CO2,particulesFines,niveauxSonores,temperature,humidite
2024-04-09 21:56:12,device123,20.843545866189686,-41.124178350240726,4529.644314049281,258.6918564139046,82.42913014338637,19.220059629322044,23.90762112715723

------------Désérialisation CSV------------
Rapport IoT pour le dispositif device123 à l'instant 2024-04-09 21:56:12
Localisation : Latitude 20.843545866189686°, Longitude -41.124178350240726°
Qualité de l'Air : CO2 4529.644314049281 ppm, Particules fines 258.6918564139046 µg/m³
Niveaux Sonores : 82.42913014338637 dB
Température : 19.220059629322044°C
Humidité : 23.90762112715723%
----------------------------------------------------------------------------------------------------
```
