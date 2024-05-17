val scalaVersionNumber = "2.13.8"

lazy val root = (project in file("."))
  .settings(
    name := "projet_data-engineer",
    version := "0.1.0-SNAPSHOT",
    scalaVersion := scalaVersionNumber,
    libraryDependencies ++= Seq(
      "io.circe" %% "circe-core" % "0.14.1",
      "io.circe" %% "circe-generic" % "0.14.1",
      "io.circe" %% "circe-parser" % "0.14.1",
      "org.apache.kafka" %% "kafka-streams-scala" % "2.8.0",
      "org.apache.kafka" % "kafka-streams" % "2.8.0",
      "org.apache.kafka" % "kafka-clients" % "2.8.0",
      "org.apache.logging.log4j" % "log4j-slf4j-impl" % "2.14.1",
      "org.apache.logging.log4j" % "log4j-api" % "2.14.1",
      "org.apache.logging.log4j" % "log4j-core" % "2.14.1",
      "com.sun.mail" % "javax.mail" % "1.6.2",
      "javax.activation" % "activation" % "1.1.1",
      "com.twilio.sdk" % "twilio" % "8.25.0" // Dépendance Twilio
    )
  )