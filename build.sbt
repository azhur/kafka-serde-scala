
lazy val `kafka-serde-scala` =
  project
    .in(file("."))
    .enablePlugins(GitVersioning)
    .aggregate(
      `kafka-serde-circe`,
      `kafka-serde-json4s`
    )
    .settings(settings)
    .settings(
      Compile / unmanagedSourceDirectories := Seq.empty,
      Test / unmanagedSourceDirectories    := Seq.empty,
      publishArtifact := false
    )

lazy val `kafka-serde-circe` = project
  .enablePlugins(AutomateHeaderPlugin)
  .settings(settings)
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
  .settings(settings)
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
  gitSettings ++
  scalafmtSettings ++
  publishSettings

lazy val commonSettings =
  Seq(
    // scalaVersion from .travis.yml via sbt-travisci
    //scalaVersion := "2.12.6",
    organization := "org.azhur",
    organizationName := "Artur Zhurat",
    startYear := Some(2018),
    licenses += ("Apache-2.0", url("http://www.apache.org/licenses/LICENSE-2.0")),
    scalacOptions ++= Seq(
      "-unchecked",
      "-deprecation",
      "-language:_",
      "-target:jvm-1.8",
      "-encoding", "UTF-8"
    ),
    Compile / unmanagedSourceDirectories := Seq((Compile / scalaSource).value),
    Test / unmanagedSourceDirectories := Seq((Test / scalaSource).value)
  )

lazy val gitSettings =
  Seq(
    git.useGitDescribe := true
  )

lazy val scalafmtSettings =
  Seq(
    scalafmtOnCompile := true
  )


lazy val publishSettings =
  Seq(
    homepage := Some(url("https://github.com/azhur/kafka-serde-scala")),
    scmInfo := Some(ScmInfo(url("https://github.com/azhur/kafka-serde-scala"),
      "git@github.com:azhur/kafka-serde-scala.git")),
    developers += Developer("azhur",
      "Artur Zhurat",
      "artur.zhurat@gmail.com",
      url("https://github.com/azhur")),
    pomIncludeRepository := (_ => false),
    bintrayPackage := "kafka-serde-scala"
  )