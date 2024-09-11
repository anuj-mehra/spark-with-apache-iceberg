// scalastyle:off
package com.spike.spark_iceberg.loader

import com.spike.spark_iceberg.core.{ApplicationConf, CredentialProvider}
import com.spike.spark_iceberg.repository.hbase.{HBaseReadRepository, HBaseWriteRepository}
import com.spike.spark_iceberg.{SparkCommandLineOptions, SparkSessionConfig}
import org.apache.hadoop.hbase.HBaseConfiguration
import org.apache.spark.sql.SparkSession

object EODTransactionsDataLoaderApp extends App with Serializable{

  println("--starting EODTransactionsDataLoaderApp---")

  val commandLineOptions = new SparkCommandLineOptions(args)

  val configFile = commandLineOptions.configFile.toOption.get
  val ppCode = commandLineOptions.ppCode.toOption.get
  val busDate = commandLineOptions.busDate.toOption.get
  val ymlFilePath = commandLineOptions.ymlFilePath.toOption.get

  val applicationConf: ApplicationConf = new ApplicationConf(ppCode, busDate, configFile)

  println(s"configFile => ${configFile}")
  println(s"ppCode => ${ppCode}")
  println(s"busDate => ${busDate}")
  println(s"ymlFilePath => ${ymlFilePath}")

  val tokenizerPassword = new CredentialProvider().getPassword(applicationConf.env, "tokenizer")

  implicit val sparkSession =
    SparkSessionConfig(s"EODTransactionsDataLoaderApp-${ppCode}-${busDate}", applicationConf.env).get

  val hbaseConf = HBaseConfiguration.create()

  val sampleHBaseReadRepo = HBaseReadRepository("schema:positions", hbaseConf)
  val sampleHBaseWriteRepo = HBaseWriteRepository("schema:positions", hbaseConf)

  val obj = new EODTransactionsDataLoaderApp(sampleHBaseReadRepo, sampleHBaseWriteRepo)

  obj.process(sparkSession)
  println("--exiting EODTransactionsDataLoaderApp---")
}

class EODTransactionsDataLoaderApp(hbaseReadRepo: HBaseReadRepository,
                                   hbaseWriteRepo: HBaseWriteRepository)  extends Serializable{

  def process(implicit sparkSession: SparkSession): Unit = {
    val data = List(("1", "a"), ("2", "b"))
    val schema = List("col1", "col2")
    //val yamlFile = ???

    import sparkSession.implicits._
    val df = data.toDF(schema:_*)
    df.show(false)
  }
}
