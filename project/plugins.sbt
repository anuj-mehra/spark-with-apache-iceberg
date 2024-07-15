// Add the required resolvers
resolvers += Resolver.sonatypeRepo("public")
resolvers += Resolver.sbtPluginRepo("releases")

// sbt-scalastyle for enforcing Scala style guidelines
addSbtPlugin("org.scalastyle" % "scalastyle-sbt-plugin" % "1.0.0")

// sbt-scalafmt for formatting Scala code
addSbtPlugin("org.scalameta" % "sbt-scalafmt" % "2.4.6")

// sbt-avrohugger for generating Scala case classes from Avro schemas
addSbtPlugin("com.julianpeeters" % "sbt-avrohugger" % "2.0.0-RC4")

// sbt-dependency-graph for visualizing dependencies
addSbtPlugin("net.virtual-void" % "sbt-dependency-graph" % "0.9.2")

// sbt-native-packager for packaging Scala applications
//addSbtPlugin("com.typesafe.sbt" % "sbt-native-packager" % "1.3.25")

// sbt-scoverage for measuring test coverage
addSbtPlugin("org.scoverage" % "sbt-scoverage" % "1.6.1")

// sbt-release for managing the release process
//addSbtPlugin("com.github.gseitz" % "sbt-release" % "1.0.8")

