// scalastyle:off
package com.spike.spark_iceberg.repository

import com.typesafe.scalalogging.LazyLogging
import org.apache.hadoop.conf.Configuration
import org.apache.hadoop.hbase.HBaseConfiguration
import org.apache.hadoop.hbase.mapred.TableInputFormat
import org.apache.spark.sql.{DataFrame, SparkSession}
import org.apache.hadoop.hbase.mapreduce.TableInputFormat

class HBaseWriteRepository(@transient conf: Configuration) extends Serializable with LazyLogging{

  def putAll(inputDf: DataFrame, rowKeyColumnName: String)
            (implicit sparkSession: SparkSession): Long ={

    val accumulator = sparkSession.sparkContext.longAccumulator("data-insert-count")

    val columns = inputDf.schema.fields

    accumulator.value
  }
}
object HBaseWriteRepository{

  def apply(tableName: String, @transient conf: Configuration): HBaseWriteRepository = {

    val config = HBaseConfiguration.create(conf)
    config.set("mapreduce.outputformat.class", "org.apache.hadoop.hbase.mapreduce.TableOutputFormat")
    config.set(TableInputFormat.INPUT_TABLE, tableName)
    config.set(TableInputFormat.OUTPUT_TABLE, tableName)
    new HBaseWriteRepository(config)
  }
}
