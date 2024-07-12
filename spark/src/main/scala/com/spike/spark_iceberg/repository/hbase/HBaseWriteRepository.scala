// scalastyle:off
package com.spike.spark_iceberg.repository.hbase

import com.typesafe.scalalogging.LazyLogging
import org.apache.hadoop.conf.Configuration
import org.apache.hadoop.hbase.HBaseConfiguration
import org.apache.hadoop.hbase.client.{Delete, Put}
import org.apache.hadoop.hbase.io.ImmutableBytesWritable
import org.apache.hadoop.hbase.mapreduce.{TableInputFormat, TableOutputFormat}
import org.apache.spark.sql.{DataFrame, SparkSession}

class HBaseWriteRepository(@transient conf: Configuration) extends Serializable with LazyLogging{

  def putAll(inputDf: DataFrame, rowKeyColumnName: String)
            (implicit sparkSession: SparkSession): Long ={

    val accumulator = sparkSession.sparkContext.longAccumulator("data-insert-count")

    val columns = inputDf.schema.fields

    val rddToSave = inputDf.rdd
      .map(row => {
        accumulator.add(Long.box(1))
        val putObject = new Put(row.getAs[String](rowKeyColumnName).getBytes())
        columns.foreach(col => {
          HBaseUtils.populatePutObject(row, putObject, col.name, col.dataType)
        })
        (new ImmutableBytesWritable(), putObject)
      })

    rddToSave.saveAsNewAPIHadoopDataset(conf)
    accumulator.value
  }


  /**
   * First Data is deleted and then the data is inserted into the hbase
   * @param inputDf
   * @param rowKeyColumnName
   * @param deleteCompleteRow
   * @param sparkSession
   * @return
   */
  def upsertAll(inputDf: DataFrame, rowKeyColumnName: String, deleteCompleteRow: Boolean  = false)
            (implicit sparkSession: SparkSession): Long ={

    val accumulator = sparkSession.sparkContext.longAccumulator("data-insert-count")

    val columns = inputDf.schema.fields

    val rddToSave = inputDf.rdd
      .map(row => {
        accumulator.add(Long.box(1))
        val putObject = new Put(row.getAs[String](rowKeyColumnName).getBytes())
        val deleteObject = new Delete(row.getAs[String](rowKeyColumnName).getBytes())

        columns.foreach(col => {
          deleteCompleteRow match {
            case true =>
              HBaseUtils.populatePutObject(row, putObject, col.name, col.dataType)
            case false =>
              HBaseUtils.populatePutAndDeleteObject(row, putObject, deleteObject, col.name, col.dataType)
          }
        })
        (new ImmutableBytesWritable(), putObject)
      })

    rddToSave.saveAsNewAPIHadoopDataset(conf)

    accumulator.value
  }

  def deleteRowsUsingRowKeys(deleteRowKeysDf: DataFrame)
                            (implicit sparkSession: SparkSession): Long = {

    val accumulator = sparkSession.sparkContext.longAccumulator("delete-rows-count")
    val columnName = deleteRowKeysDf.schema.fieldNames(0)

    val rowsToDelete = deleteRowKeysDf.rdd
      .map(row => {
        val deleteObject = new Delete(row.getAs[String](columnName).getBytes())
        accumulator.add(Long.box(1))
        (new ImmutableBytesWritable(), deleteObject)
      })

    rowsToDelete.saveAsNewAPIHadoopDataset(conf)

    accumulator.value
  }

}
object HBaseWriteRepository{

  def apply(tableName: String, @transient conf: Configuration): HBaseWriteRepository = {

    val config: Configuration = HBaseConfiguration.create(conf)
    config.set("mapreduce.outputformat.class", "org.apache.hadoop.hbase.mapreduce.TableOutputFormat")
    config.set(TableInputFormat.INPUT_TABLE, tableName)
    config.set(TableOutputFormat.OUTPUT_TABLE, tableName)
    new HBaseWriteRepository(config)
  }
}
