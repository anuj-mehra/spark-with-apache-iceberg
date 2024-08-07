// scalastyle:off
package com.spike.spark_iceberg.loader

import com.spike.spark_iceberg.core.ApplicationConf
import com.spike.spark_iceberg.{SparkCommandLineOptions, SparkSessionConfig}
import org.apache.spark.sql.SparkSession

object EODTransactionsDataLoaderApp extends App with Serializable{

  println("--starting EODTransactionsDataLoaderApp---")

  val commandLineOptions = new SparkCommandLineOptions(args)

  val configFile = commandLineOptions.configFile.toOption.get
  val ppCode = commandLineOptions.ppCode.toOption.get
  val busDate = commandLineOptions.busDate.toOption.get
  val ymlFilePath = commandLineOptions.ymlFilePath.toOption.get

  val applicationConf = new ApplicationConf(ppCode, busDate, configFile)

  println(s"configFile => ${configFile}")
  println(s"ppCode => ${ppCode}")
  println(s"busDate => ${busDate}")
  println(s"ymlFilePath => ${ymlFilePath}")

  implicit val sparkSession =
    SparkSessionConfig(s"EODTransactionsDataLoaderApp-${ppCode}-${busDate}", applicationConf.env).get

  val obj = new EODTransactionsDataLoaderApp
  obj.process(sparkSession)
  println("--exiting EODTransactionsDataLoaderApp---")
}

class EODTransactionsDataLoaderApp extends App with Serializable{

  def process(implicit sparkSession: SparkSession): Unit = {
    val data = List(("1", "a"), ("2", "b"))
    val schema = List("col1", "col2")
    //val yamlFile = ???

    import sparkSession.implicits._
    val df = data.toDF(schema:_*)
    df.show(false)
  }
}
