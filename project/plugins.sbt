addSbtPlugin("com.geirsson" % "sbt-ci-release" % "1.5.7")
addSbtPlugin("org.scalameta" % "sbt-scalafmt" % "2.4.2")
addSbtPlugin("de.heikoseeberger" % "sbt-header" % "5.6.0")
addSbtPlugin("com.thesamet" % "sbt-protoc" % "1.0.3")
addSbtPlugin("com.typesafe" % "sbt-mima-plugin" % "0.8.1")

libraryDependencies += "com.thesamet.scalapb" %% "compilerplugin" % "0.11.2"
