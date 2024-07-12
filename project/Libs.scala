import sbt._

object Versions {

  val Hadoop = "3.0.0"
  val HBase = "2.1.0"
  val Spark = "3.1.2"
}

object Libs {

  val scalaVersion = "2.12.14"

  val `scallop` = "org.rogach" %% "scallop" % "3.5.1"
  val `scala-lang-library` = "org.scala-lang" % "scala-library" % scalaVersion
  val `scala-lang-compiler` = "org.scala-lang" % "scala-compiler" % scalaVersion
  val `scala-lang-reflect` = "org.scala-lang" % "scala-reflect" % scalaVersion
}

object TestJars {
  val `scalatest` = "org.scalatest" %% "scalatest" % "3.2.15"  % Test
  val `mockito-core` = "org.mockito" % "mockito-core" % "4.8.0" % Test
  val `scalatest-plus` = "org.scalatestplus" %% "mockito-3-4" % "3.2.10.0" % Test
}

object Hadoop {

  val `hadoop-common` = "org.apache.hadoop" % "hadoop-common" % Versions.Hadoop exclude
    ("com.fasterxml.jackson.core", "jackson-databind")

  val `hadoop-hdfs` = "org.apache.hadoop" % "hadoop-hdfs" % Versions.Hadoop exclude
    ("com.fasterxml.jackson.core", "jackson-databind")

  val `hadoop-auth` = "org.apache.hadoop" % "hadoop-auth" % Versions.Hadoop exclude
    ("com.fasterxml.jackson.core", "jackson-databind")

  val `hadoop-client` = "org.apache.hadoop" % "hadoop-client" % Versions.Hadoop exclude
    ("com.fasterxml.jackson.core", "jackson-databind")

  val `hadoop-core` = "org.apache.hadoop" % "hadoop-core" % "2.6.0-mr1-cdh5.16.1.1" pomOnly()
}

object Spark {
  val `spark-core` = "org.apache.spark"  %%  "spark-core"  %  Versions.Spark excludeAll
    (ExclusionRule(organization = "xerces"), ExclusionRule(organization = "log4j"))

  val `spark-sql` = "org.apache.spark"  %%  "spark-sql"  %  Versions.Spark

  val `commons-io` = "commons-io" % "commons-io" %  "2.6" exclude("commons-logging", "commons-logging") force()

  val `spark-avro-3.1.2` = "org.apache.spark" %% "spark-avro" % "3.1.2" // Update to Spark 3.1.2

  object TestLibs {
    val `spark-catalyst-test` = "org.apache.spark"  %%  "spark-catalyst"  %  Versions.Spark % "test" classifier "tests"
    val `spark-core-test` = "org.apache.spark"  %% "spark-core"  %  Versions.Spark % "test" classifier "tests"
    val `spark-sql-test` = "org.apache.spark"  %%  "spark-sql"  %  Versions.Spark % "test" classifier "tests"
  }
}


/*object Spark {

  val `spark-core` = "org.apache.spark"  %%  "spark-core"  %  HadoopVersion.Spark excludeAll
    (ExclusionRule(organization = "xerces"), ExclusionRule(organization = "log4j"))

  val `spark-sql` = "org.apache.spark"  %%  "spark-sql"  %  HadoopVersion.Spark

  val `commons-io` = "commons-io" % "commons-io" %  "2.6" exclude("commons-logging", "commons-logging") force()
  val `spark-avro-2.4` = "org.apache.spark" %% "spark-avro" % "2.4.0"

  object TestLibs {

    val `spark-catalyst-test` = "org.apache.spark"  %%  "spark-catalyst"  %  HadoopVersion.Spark % "test" classifier "tests"
    val `spark-core-test` = "org.apache.spark"  %% "spark-core"  %  HadoopVersion.Spark % "test" classifier "tests"
    val `spark-sql-test` = "org.apache.spark"  %%  "spark-sql"  %  HadoopVersion.Spark % "test" classifier "tests"
  }
}*/

object HBase {

  val `hbase-client` = "org.apache.hbase" % "hbase-client" % Versions.HBase
  val `hbase-server` = "org.apache.hbase" % "hbase-server" % Versions.HBase
  val `hbase-common` = "org.apache.hbase" % "hbase-common" % Versions.HBase
  val `hbase-protocol` = "org.apache.hbase" % "hbase-protocol" % Versions.HBase
  val `hbase-metrics` = "org.apache.hbase" % "hbase-metrics" % Versions.HBase
  val `hbase-metrics-api` = "org.apache.hbase" % "hbase-metrics-api" % Versions.HBase
  val `hbase-http` = "org.apache.hbase" % "hbase-http" % Versions.HBase
  val `hbase-mapreduce` = "org.apache.hbase" % "hbase-mapreduce" % Versions.HBase

  object Tests {
    val `hbase-server` = "org.apache.hbase" % "hbase-server" % Versions.HBase % Test
    val `hbase-common` = "org.apache.hbase" % "hbase-common" % Versions.HBase % Test
    val `hbase-testing-util` = "org.apache.hbase" % "hbase-testing-util" % Versions.HBase % Test
  }
}

object Logging{

  val `scala-logging` = "com.typesafe.scala-logging"  %% "scala-logging" % "3.7.2"
  val `logback` = "ch.qos.logback" % "logback-classic" % "1.2.3"
  val `logback-json-encoder` = "net.logstash.logback" % "logstash-logback-encoder" % "4.11" excludeAll ExclusionRule(
    organization = "com.fasterxml.jackson.core"
  )
}

object Common {
  val `typesafe-config` = "com.typesafe" % "config" % "1.3.2"
}

object Excluded {
  val `slf4j-log4j12` = "org.slf4j" % "slf4j-log4j12"
  val `fasterxml.jackson.core1` = "com.fasterxml.jackson.core" % "jackson-annotations" % "2.9.2"
  val `fasterxml.jackson.core2` = "com.fasterxml.jackson.core" % "jackson-databind" % "2.9.2"
  val `fasterxml.jackson.core3` = "com.fasterxml.jackson.core" % "jackson-core" % "2.9.2"

  val `log4j` = "log4j"  % "log4j"  % "1.2.17"
  // https://mvnrepository.com/artifact/log4j/log4j

}