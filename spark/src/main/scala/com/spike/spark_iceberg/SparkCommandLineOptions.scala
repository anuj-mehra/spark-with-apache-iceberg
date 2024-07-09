// scalastyle:off
package com.spike.spark_iceberg

import org.rogach.scallop.ScallopConf

class SparkCommandLineOptions(args: Seq[String])  extends ScallopConf(args) {

  lazy val configFile = opt[String](short = 'a', required = true)
  lazy val ppCode = opt[String](short = 'p', required = true)
  lazy val busDate = opt[String](short = 'b', required = true)
  lazy val ymlFilePath = opt[String](short = 'y', required = false)
  lazy val kafkaTopicName = opt[String](short = 'k', required = false)

  verify()
}
