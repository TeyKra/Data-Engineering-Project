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


## Un simulateur de votre IOT affichant des rapport de votre IOT:

### Simulateur IoT

Le simulateur IoT, `SimulateurIoT`, utilise la génération aléatoire pour créer des données environnementales simulées représentant des rapports de dispositifs IoT. Il génère des rapports à intervalles réguliers entre un moment de début et de fin, simulant ainsi l'émission continue de données par les capteurs. Chaque rapport inclut des informations telles que la localisation géographique, la qualité de l'air (CO2 et particules fines), les niveaux sonores, la température, et l'humidité, avec des valeurs générées aléatoirement. La localisation est déterminée par des coordonnées latitude et longitude générées de manière aléatoire dans des plages valides globales. Ces rapports simulés sont regroupés dans une séquence, permettant une analyse ou une visualisation ultérieure des données générées.

### Code du simulateur IoT

```scala
// class : IoTDataModels.scala

object SimulateurIoT {
  val random = new Random()
  val dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")

  def genererLocalisation(): Location = Location(random.between(-90.0, 90.0), random.between(-180.0, 180.0))

  def simulerRapportIoT(deviceId: String, start: LocalDateTime, end: LocalDateTime, intervalSeconds: Int): Seq[IoTData] = {
    Iterator.iterate(start)(_.plusSeconds(intervalSeconds))
      .takeWhile(!_.isAfter(end))
      .map { currentTime =>
        IoTData(
          timestamp = currentTime.format(dateTimeFormatter),
          deviceId = deviceId,
          location = genererLocalisation(),
          qualiteAir = AirQuality(random.between(400.0, 5000.0), random.between(0.0, 500.0)),
          niveauxSonores = random.between(30.0, 130.0),
          temperature = random.between(-10.0, 40.0),
          humidite = random.between(0.0, 100.0)
        )
      }.toSeq
  }
}
```

## La case class du rapport IOT:

```scala
// class : IoTDataModels.scala

import scala.util.Random
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

case class Location(latitude: Double, longitude: Double)
case class AirQuality(CO2: Double, particulesFines: Double)
case class IoTData(
    timestamp: String,
    deviceId: String,
    location: Location,
    qualiteAir: AirQuality,
    niveauxSonores: Double,
    temperature: Double,
    humidite: Double
)
```

La `case class IoTData` encapsule les données rapportées par un dispositif IoT, structurant les informations environnementales critiques en un seul objet. Elle inclut le moment précis du rapport (`timestamp`), l'identifiant unique du dispositif (`deviceId`), et la localisation géographique (`Location`) où les données ont été recueillies. En outre, `IoTData` détaille les mesures environnementales spécifiques comme la qualité de l'air (`AirQuality`, incluant CO2 et particules fines), les niveaux sonores, la température et l'humidité, fournissant une vue complète des conditions environnementales au moment du rapport.

## Le compagnon object pouvant serialiser/deserialiser en json et csv:

```scala
// class: IotDataCsv.scala

object IoTDataCsv {
  def serializeToCsv(data: Seq[IoTData]): String = {
    val header = "timestamp,deviceId,latitude,longitude,CO2,particulesFines,niveauxSonores,temperature,humidite\n"
    val rows = data.map { d =>
      s"${d.timestamp},${d.deviceId},${d.location.latitude},${d.location.longitude},${d.qualiteAir.CO2},${d.qualiteAir.particulesFines},${d.niveauxSonores},${d.temperature},${d.humidite}"
    }.mkString("\n")

    header + rows
  }
}
```
```scala
// class: IoTDataJson.scala

import io.circe.{Encoder, Decoder}
import io.circe.generic.semiauto.{deriveEncoder, deriveDecoder}
import io.circe.parser._
import io.circe.syntax._

object IoTDataJson {
  implicit val locationEncoder: Encoder[Location] = deriveEncoder
  implicit val locationDecoder: Decoder[Location] = deriveDecoder
  implicit val airQualityEncoder: Encoder[AirQuality] = deriveEncoder
  implicit val airQualityDecoder: Decoder[AirQuality] = deriveDecoder
  implicit val ioTDataEncoder: Encoder[IoTData] = deriveEncoder
  implicit val ioTDataDecoder: Decoder[IoTData] = deriveDecoder

  def serialize(iotData: IoTData): String = iotData.asJson.noSpaces
  def deserialize(jsonString: String): Either[io.circe.Error, IoTData] = decode[IoTData](jsonString)
}
```

Les compagnons `IoTDataCsv` et `IoTDataJson` fournissent des fonctionnalités pour sérialiser et désérialiser les données des dispositifs IoT en formats CSV et JSON, respectivement, facilitant l'exportation, le stockage et le partage des données collectées.

**`IoTDataCsv`** permet la conversion d'une séquence d'objets `IoTData` en une chaîne formatée CSV, où chaque ligne représente un rapport IoT, comprenant le timestamp, l'identifiant du dispositif, les mesures environnementales, et les localisations. La première ligne est un en-tête décrivant chaque colonne pour clarifier le contenu du CSV, rendant le fichier immédiatement utilisable dans des applications de feuilles de calcul ou des systèmes de base de données.

**`IoTDataJson`**, en utilisant la bibliothèque `circe`, fournit des encodeurs et des décodeurs implicites pour convertir les objets `IoTData` (ainsi que les `Location` et `AirQuality` imbriqués) en JSON et vice-versa. La sérialisation en JSON transforme un objet `IoTData` en une chaîne JSON compacte, sans espaces inutiles, idéale pour le stockage ou l'envoi via des API web. La désérialisation permet de reconstruire un objet `IoTData` à partir d'une chaîne JSON, facilitant l'intégration et l'analyse des données collectées.

Ensemble, ces objets facilitent la manipulation des données IoT dans des formats largement adoptés et interopérables, permettant une grande flexibilité dans le traitement des données, leur visualisation, et leur analyse au sein de divers outils et plateformes.

## Output du Simulateur IoT

```
Rapports des données IoT :
===========================================================================
Rapport IoT pour le dispositif device123 à l'instant 2024-04-09 20:37:40
Localisation : Latitude -3.1108942057630884°, Longitude -100.26535833284794°
Qualité de l'Air : CO2 2208.498492161242 ppm, Particules fines 345.4976806665362 µg/m³
Niveaux Sonores : 86.75971361475887 dB
Température : 20.756255107320555°C
Humidité : 54.5442845336363%

Optionnel : Affichage en JSON et CSV

JSON:
{"timestamp":"2024-04-09 20:37:40","deviceId":"device123","location":{"latitude":-3.1108942057630884,"longitude":-100.26535833284794},"qualiteAir":{"CO2":2208.498492161242,"particulesFines":345.4976806665362},"niveauxSonores":86.75971361475887,"temperature":20.756255107320555,"humidite":54.5442845336363}

CSV:
timestamp,deviceId,latitude,longitude,CO2,particulesFines,niveauxSonores,temperature,humidite
2024-04-09 20:37:40,device123,-3.1108942057630884,-100.26535833284794,2208.498492161242,345.4976806665362,86.75971361475887,20.756255107320555,54.5442845336363
---------------------------------------------------------------------------
Rapport IoT pour le dispositif device123 à l'instant 2024-04-09 20:38:40
Localisation : Latitude 51.6876160430441°, Longitude -64.79049986857629°
Qualité de l'Air : CO2 925.0088252610932 ppm, Particules fines 278.40013453429117 µg/m³
Niveaux Sonores : 97.93805952755898 dB
Température : -9.011162234080285°C
Humidité : 70.78904259888942%

Optionnel : Affichage en JSON et CSV

JSON:
{"timestamp":"2024-04-09 20:38:40","deviceId":"device123","location":{"latitude":51.6876160430441,"longitude":-64.79049986857629},"qualiteAir":{"CO2":925.0088252610932,"particulesFines":278.40013453429117},"niveauxSonores":97.93805952755898,"temperature":-9.011162234080285,"humidite":70.78904259888942}

CSV:
timestamp,deviceId,latitude,longitude,CO2,particulesFines,niveauxSonores,temperature,humidite
2024-04-09 20:38:40,device123,51.6876160430441,-64.79049986857629,925.0088252610932,278.40013453429117,97.93805952755898,-9.011162234080285,70.78904259888942
---------------------------------------------------------------------------
Rapport IoT pour le dispositif device123 à l'instant 2024-04-09 20:39:40
Localisation : Latitude 64.04257125631892°, Longitude 167.15760736713145°
Qualité de l'Air : CO2 4218.064856404491 ppm, Particules fines 29.39782240730637 µg/m³
Niveaux Sonores : 66.2072441486125 dB
Température : 27.74751615841827°C
Humidité : 88.27562300584732%

Optionnel : Affichage en JSON et CSV

JSON:
{"timestamp":"2024-04-09 20:39:40","deviceId":"device123","location":{"latitude":64.04257125631892,"longitude":167.15760736713145},"qualiteAir":{"CO2":4218.064856404491,"particulesFines":29.39782240730637},"niveauxSonores":66.2072441486125,"temperature":27.74751615841827,"humidite":88.27562300584732}

CSV:
timestamp,deviceId,latitude,longitude,CO2,particulesFines,niveauxSonores,temperature,humidite
2024-04-09 20:39:40,device123,64.04257125631892,167.15760736713145,4218.064856404491,29.39782240730637,66.2072441486125,27.74751615841827,88.27562300584732
---------------------------------------------------------------------------
Rapport IoT pour le dispositif device123 à l'instant 2024-04-09 20:40:40
Localisation : Latitude -11.721902166266887°, Longitude 39.444283825547274°
Qualité de l'Air : CO2 1125.946858851783 ppm, Particules fines 479.6626103230859 µg/m³
Niveaux Sonores : 60.288649769163946 dB
Température : -9.90489854056594°C
Humidité : 17.331140850532112%

Optionnel : Affichage en JSON et CSV

JSON:
{"timestamp":"2024-04-09 20:40:40","deviceId":"device123","location":{"latitude":-11.721902166266887,"longitude":39.444283825547274},"qualiteAir":{"CO2":1125.946858851783,"particulesFines":479.6626103230859},"niveauxSonores":60.288649769163946,"temperature":-9.90489854056594,"humidite":17.331140850532112}

CSV:
timestamp,deviceId,latitude,longitude,CO2,particulesFines,niveauxSonores,temperature,humidite
2024-04-09 20:40:40,device123,-11.721902166266887,39.444283825547274,1125.946858851783,479.6626103230859,60.288649769163946,-9.90489854056594,17.331140850532112
---------------------------------------------------------------------------
Rapport IoT pour le dispositif device123 à l'instant 2024-04-09 20:41:40
Localisation : Latitude -40.30580780073398°, Longitude -3.6643243197236472°
Qualité de l'Air : CO2 4963.617416945673 ppm, Particules fines 379.6621254626115 µg/m³
Niveaux Sonores : 113.19943083735743 dB
Température : 24.063285251928647°C
Humidité : 81.54294528353924%

Optionnel : Affichage en JSON et CSV

JSON:
{"timestamp":"2024-04-09 20:41:40","deviceId":"device123","location":{"latitude":-40.30580780073398,"longitude":-3.6643243197236472},"qualiteAir":{"CO2":4963.617416945673,"particulesFines":379.6621254626115},"niveauxSonores":113.19943083735743,"temperature":24.063285251928647,"humidite":81.54294528353924}

CSV:
timestamp,deviceId,latitude,longitude,CO2,particulesFines,niveauxSonores,temperature,humidite
2024-04-09 20:41:40,device123,-40.30580780073398,-3.6643243197236472,4963.617416945673,379.6621254626115,113.19943083735743,24.063285251928647,81.54294528353924
---------------------------------------------------------------------------
Rapport IoT pour le dispositif device123 à l'instant 2024-04-09 20:42:40
Localisation : Latitude -58.53359094338687°, Longitude 90.79814637138668°
Qualité de l'Air : CO2 1165.6619676727291 ppm, Particules fines 386.3442240904387 µg/m³
Niveaux Sonores : 117.70376012209913 dB
Température : -6.476184376465662°C
Humidité : 16.28371124412028%

Optionnel : Affichage en JSON et CSV

JSON:
{"timestamp":"2024-04-09 20:42:40","deviceId":"device123","location":{"latitude":-58.53359094338687,"longitude":90.79814637138668},"qualiteAir":{"CO2":1165.6619676727291,"particulesFines":386.3442240904387},"niveauxSonores":117.70376012209913,"temperature":-6.476184376465662,"humidite":16.28371124412028}

CSV:
timestamp,deviceId,latitude,longitude,CO2,particulesFines,niveauxSonores,temperature,humidite
2024-04-09 20:42:40,device123,-58.53359094338687,90.79814637138668,1165.6619676727291,386.3442240904387,117.70376012209913,-6.476184376465662,16.28371124412028
---------------------------------------------------------------------------
```
