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
    Hadoop.`hadoop-auth`,
    Hadoop.`hadoop-client`,
    Hadoop.`hadoop-common`,
    Hadoop.`hadoop-core`,
    Hadoop.`hadoop-hdfs`,
    Hadoop.`hadoop-hdfs-client`,
    Common.`typesafe-config`,
    Libs.`scallop`,
    Libs.`scala-lang-library`,
    Libs.`scala-lang-compiler`,
    Libs.`scala-lang-reflect`,
    TestJars.`mockito-core`,
    TestJars.`scalatest`,
    TestJars.`scalatest-plus`,
    HBase.`hbase-client`,
    HBase.`hbase-common`,
    HBase.`hbase-http`,
    HBase.`hbase-mapreduce`,
    HBase.`hbase-metrics-api`,
    HBase.`hbase-metrics`,
    HBase.`hbase-protocol`,
    HBase.`hbase-server`,
    HBase.Tests.`hbase-testing-util`,
    HBase.Tests.`hbase-common`,
    HBase.Tests.`hbase-server`,
    HBase.Tests.`hadoop-hdfs`,
    HBase.Tests.`hbase-hadoop-compat`,
    HBase.Tests.`hbase-hadoop2-compat-util`,
    HBase.Tests.`hbase-mapreduce-tests`,
    HBase.Tests.`hbase-zookeeper-tests`,
    HBase.Tests.`hadoop-common`,
    // Avro.`spark-avro`, // Check if this is required or not? Only added for avsc generator.
    Yaml.`yaml-parser`,
    Yaml.`snakeyaml`,
    Excluded.`log4j`
  )

  val logging = Seq(Logging.`scala-logging`, Logging.`logback`, Logging.`logback-json-encoder`)

  val excludedJackson: scala.collection.mutable.Seq[ModuleID] = scala.collection.mutable.Seq(
    Excluded.`fasterxml.jackson.core1`,
    Excluded.`fasterxml.jackson.core2`,
    Excluded.`fasterxml.jackson.core3`
  )

  val excluded = Seq(Excluded.`slf4j-log4j12`)
}