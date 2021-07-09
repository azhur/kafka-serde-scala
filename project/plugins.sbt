addSbtPlugin("com.geirsson" % "sbt-ci-release" % "1.5.7")
addSbtPlugin("org.scalameta" % "sbt-scalafmt" % "2.4.3")
addSbtPlugin("de.heikoseeberger" % "sbt-header" % "5.6.0")
addSbtPlugin("com.thesamet" % "sbt-protoc" % "1.0.4")
addSbtPlugin("com.typesafe" % "sbt-mima-plugin" % "0.9.2")

libraryDependencies += "com.thesamet.scalapb" %% "compilerplugin" % "0.11.4"
