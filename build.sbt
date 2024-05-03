val scalaVersionNumber = "2.13.8"  // Renommez pour éviter toute confusion potentielle avec la clé scalaVersion

lazy val root = (project in file("."))
  .settings(
    name := "projet_data-engineer",
    version := "0.1.0-SNAPSHOT",
    scalaVersion := scalaVersionNumber,  // Utilisez la variable renommée ici
    libraryDependencies ++= Seq(
      "io.circe" %% "circe-core" % "0.14.1",
      "io.circe" %% "circe-generic" % "0.14.1",
      "io.circe" %% "circe-parser" % "0.14.1",
      "org.apache.kafka" %% "kafka-streams-scala" % "2.8.0",
      "org.apache.kafka" % "kafka-streams" % "2.8.0",
      "org.apache.kafka" % "kafka-clients" % "2.8.0",
      "org.apache.logging.log4j" % "log4j-slf4j-impl" % "2.14.1",
      "org.apache.logging.log4j" % "log4j-api" % "2.14.1",
      "org.apache.logging.log4j" % "log4j-core" % "2.14.1"
    )
  )
