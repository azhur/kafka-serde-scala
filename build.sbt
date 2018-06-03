
lazy val `kafka-serde-scala` =
  project
    .in(file("."))
    .aggregate(
      `kafka-serde-circe`,
      `kafka-serde-json4s`
    )
    .settings(commonSettings)
    .settings(scalafmtSettings)
    .settings(noPublishSettings)
    .settings(
      Compile / unmanagedSourceDirectories := Seq.empty,
      Test / unmanagedSourceDirectories    := Seq.empty
    )

lazy val `kafka-serde-circe` = project
  .enablePlugins(AutomateHeaderPlugin)
  .settings(commonSettings)
  .settings(scalafmtSettings)
  .settings(publishSettings)
  .settings(
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
  .settings(scalafmtSettings)
  .settings(publishSettings)
  .settings(
    libraryDependencies ++= Seq(
      dependency.kafkaClients,
      dependency.json4sCore,
      dependency.json4sJackson % Test,
      dependency.json4sNative  % Test,
      dependency.scalaTest    % Test
    )
  )

lazy val dependency =
  new {
    object Version {
      val circe         = "0.9.3"
      val json4s        = "3.5.4"
      val scalaTest     = "3.0.5"
      val kafka         = "1.1.0"
    }
    val kafkaClients        = "org.apache.kafka"                      %  "kafka-clients"        % Version.kafka
    val circe               = "io.circe"                              %% "circe-core"           % Version.circe
    val circeParser         = "io.circe"                              %% "circe-parser"         % Version.circe
    val circeJawn           = "io.circe"                              %% "circe-jawn"           % Version.circe
    val circeGeneric        = "io.circe"                              %% "circe-generic"        % Version.circe
    val json4sCore          = "org.json4s"                            %% "json4s-core"          % Version.json4s
    val json4sJackson       = "org.json4s"                            %% "json4s-jackson"       % Version.json4s
    val json4sNative        = "org.json4s"                            %% "json4s-native"        % Version.json4s
    val scalaTest           = "org.scalatest"                         %% "scalatest"            % Version.scalaTest
  }


lazy val settings =
  commonSettings ++
  scalafmtSettings ++
  publishSettings

lazy val commonSettings =
  Seq(
    resolvers += "Sonatype OSS Staging" at "https://oss.sonatype.org/content/repositories/staging",
    scalaVersion := "2.12.6",
    organization := "io.github.azhur",
    organizationName := "Artur Zhurat",
    organizationHomepage := Some(url("https://github.com/azhur")),
    startYear := Some(2018),
    licenses += ("Apache-2.0", url("http://www.apache.org/licenses/LICENSE-2.0")),
    scalacOptions ++= Seq(
      "-unchecked",
      "-deprecation",
      "-language:_",
      "-target:jvm-1.8",
      "-encoding", "UTF-8"
    ),
    developers := List(
      Developer(
        id = "azhur",
        name = "Artur Zhurat",
        email = "artur.zhurat@gmail.com",
        url = url("https://twitter.com/a_zhur")
      )
    ),
    Compile / unmanagedSourceDirectories := Seq((Compile / scalaSource).value),
    Test / unmanagedSourceDirectories := Seq((Test / scalaSource).value)
  )

lazy val scalafmtSettings =
  Seq(
    scalafmtOnCompile := true
  )


lazy val noPublishSettings = Seq(
  skip in publish := true,
  publishTo := Some(if (isSnapshot.value) Opts.resolver.sonatypeSnapshots else Opts.resolver.sonatypeStaging)
)

lazy val publishSettings = Seq(
  publishTo := Some(if (isSnapshot.value) Opts.resolver.sonatypeSnapshots else Opts.resolver.sonatypeStaging),
  sonatypeProfileName := "io.github.azhur",
  scmInfo := Some(
    ScmInfo(
      url("https://github.com/azhur/kafka-serde-scala"),
      "scm:git@github.com:azhur/kafka-serde-scala.git"
    )
  ),
  publishMavenStyle := true,
  pomIncludeRepository := { _ => false }
)