import sbtassembly.MergeStrategy

name := "spark-with-apache-iceberg"

version := "0.1"

scalaVersion := "2.12.14"

updateOptions := updateOptions.value
  .withCachedResolution(true)
  .withGigahorse(false)
  .withInterProjectFirst(false)
  .withLatestSnapshots(true)

lazy val compileScalastyle = taskKey[Unit]("compileScalastyle")

lazy val `spark-with-apache-iceberg` = project.in(file("."))
  .aggregate(`spark`)
  .settings(
    compileScalastyle := scalastyle.in(Compile).toTask("").value,
    (compile in Compile) := ((compile in Compile) dependsOn compileScalastyle).value,
    scalafmtOnCompile := true
  )

lazy val aggregatedProjects: Seq[ProjectReference] = Seq(spark)

lazy val `spark` = project
  .settings(
    libraryDependencies ++= Dependencies.SparkModule,
    libraryDependencies ++= Dependencies.logging,
    excludeDependencies ++= Dependencies.excludedJackson,
    excludeDependencies ++= Dependencies.excluded,
    assemblySettings
  )


lazy val assemblySettings = Seq(
  test in assembly := {},
  assemblyJarName in assembly := s"spark-with-apache-iceberg-${version.value}.jar",
  assemblyMergeStrategy in assembly := {
    case PathList("META-INF", xs@_*) => xs.map(_.toLowerCase) match {
      case "manifest.mf" :: Nil |
           "index.list" :: Nil |
           "dependencies" :: Nil |
           "license" :: Nil |
           "dummy.dsa" :: Nil |
           "notice" :: Nil => MergeStrategy.discard
      case _ => MergeStrategy.first
    }
    case _ => MergeStrategy.first
  }
)

unmanagedClasspath in Test += baseDirectory.value / "src/main/resources"
