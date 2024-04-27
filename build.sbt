inThisBuild(
  Seq(
    organization         := "io.github.azhur",
    organizationName     := "Artur Zhurat",
    organizationHomepage := Some(url("https://github.com/azhur")),
    homepage             := Some(url("https://github.com/azhur/kafka-serde-scala")),
    licenses             := List("Apache-2.0" -> url("http://www.apache.org/licenses/LICENSE-2.0")),
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
    )
  )
)

lazy val latest212 = "2.12.18"

lazy val latest213 = "2.13.12"

lazy val latest3 = "3.3.1"

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
      `kafka-serde-zio-json`,
      `kafka-serde-scala-example`
    )
    .settings(commonSettings)
    .settings(
      Compile / unmanagedSourceDirectories := Seq.empty,
      Test / unmanagedSourceDirectories    := Seq.empty,
      publishArtifact                      := false
    )

lazy val `kafka-serde-circe` = project
  .enablePlugins(AutomateHeaderPlugin)
  .settings(commonSettings)
  .settings(mimaSettings)
  .settings(
    crossScalaVersions := Seq(latest212, latest213, latest3),
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
    crossScalaVersions := Seq(latest212, latest213, latest3),
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
    crossScalaVersions := Seq(latest212, latest213, latest3),
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
    crossScalaVersions := Seq(latest212, latest213),
    libraryDependencies ++= Seq(
      dependency.kafkaClients,
      dependency.playJson,
      dependency.scalaTest % Test
    )
  )

lazy val `kafka-serde-upickle` = project
  .enablePlugins(AutomateHeaderPlugin)
  .settings(commonSettings)
  .settings(mimaSettings)
  .settings(
    crossScalaVersions := Seq(latest212, latest213, latest3),
    libraryDependencies ++= Seq(
      dependency.kafkaClients,
      dependency.upickle,
      dependency.scalaTest % Test
    )
  )

lazy val `kafka-serde-avro4s` = project
  .enablePlugins(AutomateHeaderPlugin)
  .settings(commonSettings)
  .settings(mimaSettings)
  .settings(
    crossScalaVersions := Seq(latest212, latest213),
    libraryDependencies ++= Seq(
      dependency.kafkaClients,
      dependency.avro4sCore,
      dependency.avro4sKafka,
      dependency.scalaTest % Test
    )
  )

lazy val `kafka-serde-jackson` = project
  .enablePlugins(AutomateHeaderPlugin)
  .settings(commonSettings)
  .settings(mimaSettings)
  .settings(
    crossScalaVersions := Seq(latest212, latest213),
    libraryDependencies ++= Seq(
      dependency.kafkaClients,
      dependency.jacksonCore,
      dependency.jacksonScala,
      "org.scala-lang"           % "scala-reflect" % scalaVersion.value,
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
    crossScalaVersions := Seq(latest212, latest213, latest3),
    startYear          := Some(2021),
    libraryDependencies ++= Seq(
      dependency.kafkaClients,
      dependency.scalaTest % Test
    )
  )
  .settings(
    Compile / PB.targets := Seq(
      scalapb.gen() -> (Compile / sourceManaged).value / "scalapb"
    ),
    Test / PB.targets := Seq(
      scalapb.gen() -> (Test / sourceManaged).value / "scalapb"
    )
  )

lazy val `kafka-serde-zio-json` = project
  .enablePlugins(AutomateHeaderPlugin)
  .settings(commonSettings)
  .settings(mimaSettings)
  .settings(
    crossScalaVersions := Seq(latest212, latest213, latest3),
    libraryDependencies ++= Seq(
      dependency.kafkaClients,
      dependency.zioJson,
      dependency.scalaTest % Test
    )
  )

lazy val `kafka-serde-scala-example` = project
  .dependsOn(`kafka-serde-circe`)
  .enablePlugins(AutomateHeaderPlugin)
  .disablePlugins(MimaPlugin)
  .settings(commonSettings)
  .settings(
    publishArtifact := false,
    libraryDependencies ++= Seq(
      dependency.kafkaStreamsScala,
      dependency.circeGeneric,
      dependency.scalaTest % Test
    )
  )

lazy val dependency =
  new {
    object Version {
      val avro4s        = "4.1.1"
      val circe         = "0.14.7"
      val json4s        = "4.0.7"
      val jsoniterScala = "2.28.1"
      val scalaTest     = "3.2.17"
      val kafka         = "3.6.1"
      val play          = "3.0.2"
      val upickle       = "3.1.4"
      val jackson       = "2.16.1"
      val zioJson       = "0.6.2"
    }
    val kafkaClients        = "org.apache.kafka"                       % "kafka-clients"         % Version.kafka
    val kafkaStreamsScala   = "org.apache.kafka"                      %% "kafka-streams-scala"   % Version.kafka
    val avro4sCore          = "com.sksamuel.avro4s"                   %% "avro4s-core"           % Version.avro4s
    val avro4sKafka         = "com.sksamuel.avro4s"                   %% "avro4s-kafka"          % Version.avro4s
    val circe               = "io.circe"                              %% "circe-core"            % Version.circe
    val circeParser         = "io.circe"                              %% "circe-parser"          % Version.circe
    val circeJawn           = "io.circe"                              %% "circe-jawn"            % Version.circe
    val circeGeneric        = "io.circe"                              %% "circe-generic"         % Version.circe
    val json4sCore          = "org.json4s"                            %% "json4s-core"           % Version.json4s
    val json4sJackson       = "org.json4s"                            %% "json4s-jackson"        % Version.json4s
    val json4sNative        = "org.json4s"                            %% "json4s-native"         % Version.json4s
    val jsoniterScalaCore   = "com.github.plokhotnyuk.jsoniter-scala" %% "jsoniter-scala-core"   % Version.jsoniterScala
    val jsoniterScalaMacros = "com.github.plokhotnyuk.jsoniter-scala" %% "jsoniter-scala-macros" % Version.jsoniterScala
    val playJson            = "org.playframework"                     %% "play-json"             % Version.play
    val upickle             = "com.lihaoyi"                           %% "upickle"               % Version.upickle
    val jacksonScala        = "com.fasterxml.jackson.module"          %% "jackson-module-scala"  % Version.jackson
    val jacksonCore         = "com.fasterxml.jackson.core"             % "jackson-core"          % Version.jackson
    val zioJson             = "dev.zio"                               %% "zio-json"              % Version.zioJson
    val jacksonProtobuf = "com.fasterxml.jackson.dataformat" % "jackson-dataformat-protobuf" % Version.jackson
    val jacksonAvro     = "com.fasterxml.jackson.dataformat" % "jackson-dataformat-avro"     % Version.jackson
    val scalaTest       = "org.scalatest"                   %% "scalatest"                   % Version.scalaTest
  }

lazy val commonSettings =
  Seq(
    resolvers += "Sonatype OSS Staging".at("https://oss.sonatype.org/content/repositories/staging"),
    scalaVersion := latest213,
    startYear    := Some(2018),
    scalacOptions ++= Seq(
      "-unchecked",
      "-deprecation",
      "-Xfatal-warnings",
      "-encoding",
      "UTF-8"
    ) ++ (CrossVersion.partialVersion(scalaVersion.value) match {
      case Some((2, 12)) => Seq("-target:jvm-1.8", "-language:_", "-Ywarn-dead-code", "-Xlint")
      case Some((2, 13)) => Seq("-language:_", "-Ywarn-dead-code", "-Xlint", "-Xlint:-byname-implicit")
      case _             => Seq.empty
    }),
    pomIncludeRepository := (_ => false),
    scalafmtOnCompile    := true
  )

lazy val mimaSettings =
  Seq(
    mimaPreviousArtifacts := previousStableVersion.value.map(organization.value %% name.value % _).toSet
  )
