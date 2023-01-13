addSbtPlugin("com.github.sbt"    % "sbt-ci-release"  % "1.5.10")
addSbtPlugin("org.scalameta"     % "sbt-scalafmt"    % "2.5.0")
addSbtPlugin("de.heikoseeberger" % "sbt-header"      % "5.6.5")
addSbtPlugin("com.thesamet"      % "sbt-protoc"      % "1.0.6")
addSbtPlugin("com.typesafe"      % "sbt-mima-plugin" % "1.0.1")

libraryDependencies += "com.thesamet.scalapb" %% "compilerplugin" % "0.11.8"
