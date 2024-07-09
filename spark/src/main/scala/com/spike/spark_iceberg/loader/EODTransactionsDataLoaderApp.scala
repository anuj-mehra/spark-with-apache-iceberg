// scalastyle:off
package com.spike.spark_iceberg.loader

import com.spike.spark_iceberg.{SparkCommandLineOptions, SparkSessionConfig}
import org.apache.spark.sql.SparkSession

object EODTransactionsDataLoaderApp extends App with Serializable{

  println("--starting EODTransactionsDataLoaderApp---")

  val commandLineOptions = new SparkCommandLineOptions(args)

  val configFile = commandLineOptions.configFile.toOption.get
  val ppCode = commandLineOptions.ppCode.toOption.get
  val busDate = commandLineOptions.busDate.toOption.get
  val ymlFilePath = commandLineOptions.ymlFilePath.toOption.get

  println(s"configFile => ${configFile}")
  println(s"ppCode => ${ppCode}")
  println(s"busDate => ${busDate}")
  println(s"ymlFilePath => ${ymlFilePath}")

  val implicit sparkSession = SparkSessionConfig(s"EODTransactionsDataLoaderApp-${ppCode}-${busDate}", )

  println("--exiting EODTransactionsDataLoaderApp---")
}

class EODTransactionsDataLoaderApp extends App with Serializable{

  def process()(implicit sparkSession: SparkSession): Unit = {

  }
}
