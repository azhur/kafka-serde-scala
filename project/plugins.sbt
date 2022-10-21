addSbtPlugin("com.github.sbt"    % "sbt-ci-release"  % "1.5.10")
addSbtPlugin("org.scalameta"     % "sbt-scalafmt"    % "2.4.6")
addSbtPlugin("de.heikoseeberger" % "sbt-header"      % "5.8.0")
addSbtPlugin("com.thesamet"      % "sbt-protoc"      % "1.0.6")
addSbtPlugin("com.typesafe"      % "sbt-mima-plugin" % "1.0.1")

libraryDependencies += "com.thesamet.scalapb" %% "compilerplugin" % "0.11.8"
