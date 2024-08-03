// scalastyle:off
package com.spike.spark_iceberg.repository.spark

import com.typesafe.scalalogging.LazyLogging
import org.apache.spark.rdd.RDD
import org.apache.spark.sql.{DataFrame, SparkSession}

class SparkReadRepository(sparkSession: SparkSession) extends Serializable with LazyLogging {

  def readAvroFile(path: String): DataFrame ={
    sparkSession.read.format("avro").load(path)
  }

  def readTextFile(path: String): RDD[String] = {
    sparkSession.sparkContext.textFile(path)
  }

  def readParquet(path: String): DataFrame = {
    sparkSession.read.parquet(path)
  }
}
object SparkReadRepository {

  def apply(sparkSession: SparkSession) = new SparkReadRepository(sparkSession)

}
