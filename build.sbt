inThisBuild(
  Seq(
    organization := "io.github.azhur",
    organizationName := "Artur Zhurat",
    organizationHomepage := Some(url("https://github.com/azhur")),
    homepage := Some(url("https://github.com/azhur/kafka-serde-scala")),
    licenses := List("Apache-2.0" -> url("http://www.apache.org/licenses/LICENSE-2.0")),
    scmInfo := Some(
      ScmInfo(
        url("https://github.com/azhur/kafka-serde-scala"),
        "git@github.com:azhur/kafka-serde-scala.git"
      )
    ),
    developers := List(
      Developer(
        id = "azhur",
        name = "Artur Zhurat",
        email = "artur.zhurat@gmail.com",
        url = url("https://twitter.com/a_zhur")
      )
    ),
  )
)

lazy val latest211 = "2.11.12"

lazy val latest212 = "2.12.13"

lazy val latest213 = "2.13.4"

lazy val `kafka-serde-scala` =
  project
    .in(file("."))
    .disablePlugins(MimaPlugin)
    .aggregate(
      `kafka-serde-avro4s`,
      `kafka-serde-circe`,
      `kafka-serde-jackson`,
      `kafka-serde-json4s`,
      `kafka-serde-jsoniter-scala`,
      `kafka-serde-play-json`,
      `kafka-serde-upickle`,
      `kafka-serde-scalapb`,
      `kafka-serde-scala-example`
    )
    .settings(commonSettings)
    .settings(
      Compile / unmanagedSourceDirectories := Seq.empty,
      Test / unmanagedSourceDirectories    := Seq.empty,
      publishArtifact := false
    )

lazy val `kafka-serde-circe` = project
  .enablePlugins(AutomateHeaderPlugin)
  .settings(commonSettings)
  .settings(mimaSettings)
  .settings(
    crossScalaVersions := Seq(latest213, latest212/*, latest211 Circe dropped Scala 2.11 support */),
    libraryDependencies ++= Seq(
      dependency.kafkaClients,
      dependency.circe,
      dependency.circeParser,
      dependency.circeJawn,
      dependency.circeGeneric % Test,
      dependency.scalaTest    % Test
    )
  )

lazy val `kafka-serde-json4s` = project
  .enablePlugins(AutomateHeaderPlugin)
  .settings(commonSettings)
  .settings(mimaSettings)
  .settings(
    crossScalaVersions := Seq(latest213, latest212, latest211),
    libraryDependencies ++= Seq(
      dependency.kafkaClients,
      dependency.json4sCore,
      dependency.json4sJackson % Test,
      dependency.json4sNative  % Test,
      dependency.scalaTest     % Test
    )
  )

lazy val `kafka-serde-jsoniter-scala` = project
  .enablePlugins(AutomateHeaderPlugin)
  .settings(commonSettings)
  .settings(mimaSettings)
  .settings(
    crossScalaVersions := Seq(latest213, latest212, latest211),
    libraryDependencies ++= Seq(
      dependency.kafkaClients,
      dependency.jsoniterScalaCore,
      dependency.jsoniterScalaMacros % Test,
      dependency.scalaTest           % Test
    )
  )

lazy val `kafka-serde-play-json` = project
  .enablePlugins(AutomateHeaderPlugin)
  .settings(commonSettings)
  .settings(mimaSettings)
  .settings(
    crossScalaVersions := Seq(latest213, latest212/*, latest211 Play-JSON dropped Scala 2.11 support */),
    libraryDependencies ++= Seq(
      dependency.kafkaClients,
      dependency.playJson,
      dependency.scalaTest     % Test
    )
  )

lazy val `kafka-serde-upickle` = project
  .enablePlugins(AutomateHeaderPlugin)
  .settings(commonSettings)
  .settings(mimaSettings)
  .settings(
    crossScalaVersions := Seq(latest213, latest212/*, latest211 uPickle dropped Scala 2.11 support */),
    libraryDependencies ++= Seq(
      dependency.kafkaClients,
      dependency.upickle,
      dependency.scalaTest     % Test
    )
  )

lazy val `kafka-serde-avro4s` = project
  .enablePlugins(AutomateHeaderPlugin)
  .settings(commonSettings)
  .settings(mimaSettings)
  .settings(
    crossScalaVersions := Seq(latest213, latest212),
    libraryDependencies ++= Seq(
      dependency.kafkaClients,
      dependency.avro4sCore,
      dependency.avro4sKafka,
      dependency.scalaTest     % Test
    )
  )

lazy val `kafka-serde-jackson` = project
  .enablePlugins(AutomateHeaderPlugin)
  .settings(commonSettings)
  .settings(mimaSettings)
  .settings(
    crossScalaVersions := Seq(latest213, latest212, latest211),
    libraryDependencies ++= Seq(
      dependency.kafkaClients,
      dependency.jacksonCore,
      dependency.jacksonScala,
      "org.scala-lang" % "scala-reflect" % scalaVersion.value,
      dependency.jacksonProtobuf % Test,
      dependency.jacksonAvro     % Test,
      dependency.scalaTest       % Test
    )
  )

lazy val `kafka-serde-scalapb` = project
  .enablePlugins(AutomateHeaderPlugin)
  .settings(commonSettings)
  .settings(mimaSettings)
  .settings(
    crossScalaVersions := Seq(latest213, latest212),
    startYear := Some(2021),
    libraryDependencies ++= Seq(
      dependency.kafkaClients,
      dependency.scalaTest     % Test
    )
  )
  .settings(
    Compile / PB.targets := Seq(
      scalapb.gen() -> (Compile / sourceManaged).value / "scalapb"
    )
  )

lazy val `kafka-serde-scala-example` = project
  .dependsOn(`kafka-serde-circe`)
  .enablePlugins(AutomateHeaderPlugin)
  .disablePlugins(MimaPlugin)
  .settings(commonSettings)
  .settings(
    publishArtifact := false,
    crossScalaVersions := Seq(latest213, latest212/*, latest211 Circe dropped Scala 2.11 support */),
    libraryDependencies ++= Seq(
      dependency.kafkaStreamsScala,
      dependency.circeGeneric,
      dependency.scalaTest        % Test
    )
  )

lazy val dependency =
  new {
    object Version {
      val avro4s                        = "4.0.9"
      val circe                         = "0.14.1"
      val json4s                        = "4.0.1"
      val jsoniterScala                 = "2.9.0"
      val scalaTest                     = "3.2.9"
      val kafka                         = "2.8.0"
      val play                          = "2.9.2"
      val upickle                       = "1.3.15"
      val jackson                       = "2.12.3"
    }
    val kafkaClients        = "org.apache.kafka"                      %  "kafka-clients"                    % Version.kafka
    val kafkaStreamsScala   = "org.apache.kafka"                      %% "kafka-streams-scala"              % Version.kafka
    val avro4sCore          = "com.sksamuel.avro4s"                   %% "avro4s-core"                      % Version.avro4s
    val avro4sKafka         = "com.sksamuel.avro4s"                   %% "avro4s-kafka"                     % Version.avro4s
    val circe               = "io.circe"                              %% "circe-core"                       % Version.circe
    val circeParser         = "io.circe"                              %% "circe-parser"                     % Version.circe
    val circeJawn           = "io.circe"                              %% "circe-jawn"                       % Version.circe
    val circeGeneric        = "io.circe"                              %% "circe-generic"                    % Version.circe
    val json4sCore          = "org.json4s"                            %% "json4s-core"                      % Version.json4s
    val json4sJackson       = "org.json4s"                            %% "json4s-jackson"                   % Version.json4s
    val json4sNative        = "org.json4s"                            %% "json4s-native"                    % Version.json4s
    val jsoniterScalaCore   = "com.github.plokhotnyuk.jsoniter-scala" %% "jsoniter-scala-core"              % Version.jsoniterScala
    val jsoniterScalaMacros = "com.github.plokhotnyuk.jsoniter-scala" %% "jsoniter-scala-macros"            % Version.jsoniterScala
    val playJson            = "com.typesafe.play"                     %% "play-json"                        % Version.play
    val upickle             = "com.lihaoyi"                           %% "upickle"                          % Version.upickle
    val jacksonScala        = "com.fasterxml.jackson.module"          %% "jackson-module-scala"             % Version.jackson
    val jacksonCore         = "com.fasterxml.jackson.core"            %  "jackson-core"                     % Version.jackson
    val jacksonProtobuf     = "com.fasterxml.jackson.dataformat"      %  "jackson-dataformat-protobuf"      % Version.jackson
    val jacksonAvro         = "com.fasterxml.jackson.dataformat"      %  "jackson-dataformat-avro"          % Version.jackson
    val scalaTest           = "org.scalatest"                         %% "scalatest"                        % Version.scalaTest
  }

lazy val commonSettings =
  Seq(
    resolvers += "Sonatype OSS Staging" at "https://oss.sonatype.org/content/repositories/staging",
    scalaVersion := latest213,
    startYear := Some(2018),
    scalacOptions ++= Seq(
      "-unchecked",
      "-deprecation",
      "-language:_",
      "-target:jvm-1.8",
      "-encoding", "UTF-8"
    ),
    pomIncludeRepository := (_ => false),
    scalafmtOnCompile := true
  )

  lazy val mimaSettings = 
    Seq(
      mimaPreviousArtifacts := previousStableVersion.value.map(organization.value %% name.value % _).toSet
    )
