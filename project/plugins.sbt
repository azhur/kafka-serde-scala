addSbtPlugin("com.github.sbt"    % "sbt-ci-release"  % "1.9.2")
addSbtPlugin("org.scalameta"     % "sbt-scalafmt"    % "2.5.3")
addSbtPlugin("de.heikoseeberger" % "sbt-header"      % "5.10.0")
addSbtPlugin("com.thesamet"      % "sbt-protoc"      % "1.0.7")
addSbtPlugin("com.typesafe"      % "sbt-mima-plugin" % "1.1.4")

libraryDependencies += "com.thesamet.scalapb" %% "compilerplugin" % "0.11.17"
