// scalastyle:off
package com.spike.spark_iceberg

import org.apache.spark.sql.SparkSession

class SparkSessionConfig(jobName: String, envName: String, isStreamingApp: Boolean = false) {

  def get: SparkSession = {
    val sparkSessionBuilder: SparkSession.Builder = SparkSession
      .builder
      .appName(jobName)

    //sparkSessionBuilder.master("local[*]")
    envName match {
      case "dev" =>
        sparkSessionBuilder.master("local[*]")
      case "_" =>
    }

    isStreamingApp match {
      case true =>
        sparkSessionBuilder.config("spark.streaming.stopGracefullyOnShutdown", "true")
      case false =>
    }

    sparkSessionBuilder.config("spark.driver.maxResultSize",0)
    sparkSessionBuilder.config("spark.sql.broadcastTimeout","36000")
    sparkSessionBuilder.config("spark.sql.autoBroadcastJoinThreshold",-1)

    val spark = sparkSessionBuilder.getOrCreate()
    spark.sparkContext.hadoopConfiguration.set("avro.mapred.ignore.inputs.without.extension","false")
    spark.conf.set("spark.sql.avro.compression.codec", "snappy")

    spark
  }
}

object SparkSessionConfig {

  def apply(jobName: String, envName: String) = new SparkSessionConfig(jobName,envName)
  def apply(jobName: String, envName: String,isSteamingApp: Boolean) = new SparkSessionConfig(jobName,envName, isSteamingApp)

}
