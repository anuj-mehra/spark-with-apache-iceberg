import sbt.librarymanagement.ModuleID

object Dependencies {

  var SparkModule: scala.collection.mutable.Seq[ModuleID] = scala.collection.mutable.Seq(
    Spark.`spark-core`,
    Spark.`spark-sql`,
    Spark.`spark-avro-3.1.2`,
    Spark.`commons-io`,
    Spark.TestLibs.`spark-catalyst-test`,
    Spark.TestLibs.`spark-core-test`,
    Spark.TestLibs.`spark-sql-test`,
    Common.`typesafe-config`,
    Libs.`scallop`,
    Libs.`scala-lang-library`,
    Libs.`scala-lang-compiler`,
    Libs.`scala-lang-reflect`,
    Excluded.`log4j`
  )

  val logging = Seq(Logging.`scala-logging`, Logging.`logback`, Logging.`logback-json-encoder`)

  val excludedJackson: scala.collection.mutable.Seq[ModuleID] = scala.collection.mutable.Seq(
    Excluded.`fasterxml.jackson.core1`,
    Excluded.`fasterxml.jackson.core2`,
    Excluded.`fasterxml.jackson.core3`,
  )

  val excluded = Seq(Excluded.`slf4j-log4j12`)
}