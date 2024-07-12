// scalastyle:off
package com.spike.spark_iceberg.repository.hbase

import com.typesafe.scalalogging.LazyLogging
import org.apache.hadoop.conf.Configuration
import org.apache.hadoop.hbase.TableName
import org.apache.hadoop.hbase.client.{Connection, ConnectionFactory, Delete, Put, Table}
import org.apache.hadoop.hbase.mapreduce.TableInputFormat
import org.apache.hadoop.hbase.mapreduce.TableOutputFormat
import org.apache.hadoop.hbase.util.Bytes
import org.apache.spark.sql.Row
import org.apache.spark.sql.types.{DataType, LongType, StringType}

object HBaseUtils extends Serializable with LazyLogging {

  private val columnFamily: String = "cf"

  def populatePutObject(row: Row,
                        putObject: Put,
                        hbaseColumnName: String,
                        colDataType: DataType): Unit = {

    val columnFamilyBytes = columnFamily.getBytes()
    val hbaseColumnNameBytes = hbaseColumnName.getBytes()

    colDataType match {
      case StringType =>
        Option(row.getAs[String](hbaseColumnName)) match {
          case Some(r) =>
            putObject.addColumn(columnFamilyBytes, hbaseColumnNameBytes, Bytes.toBytes(r))
          case _ =>
        }
      case LongType =>
        Option(row.getAs[Long](hbaseColumnName)) match {
          case Some(r) =>
            putObject.addColumn(columnFamilyBytes, hbaseColumnNameBytes, Bytes.toBytes(r))
          case _ =>
        }
    }

  }

  def populatePutAndDeleteObject(row: Row,
                                 putObject: Put,
                                 deleteObject: Delete,
                                 hbaseColumnName: String,
                                 colDataType: DataType): Unit = {

    val columnFamilyBytes = columnFamily.getBytes()
    val hbaseColumnNameBytes = hbaseColumnName.getBytes()

    colDataType match {
      case StringType =>
        Option(row.getAs[String](hbaseColumnName)) match {
          case Some(r) =>
            putObject.addColumn(columnFamilyBytes, hbaseColumnNameBytes, Bytes.toBytes(r))
          case _ =>
            deleteObject.addColumn(columnFamilyBytes, hbaseColumnNameBytes)
        }
      case LongType =>
        Option(row.getAs[Long](hbaseColumnName)) match {
          case Some(r) =>
            putObject.addColumn(columnFamilyBytes, hbaseColumnNameBytes, Bytes.toBytes(r))
          case _ =>
            deleteObject.addColumn(columnFamilyBytes, hbaseColumnNameBytes)
        }
    }
  }

  def getTable(connectionConf: HBaseConnectionConfig, conf: Configuration): Table = {

    conf.set(HBaseConnectionConfig.ZookeeperQuorum, connectionConf.quorum)
    conf.setInt(HBaseConnectionConfig.ZookeeperPort, connectionConf.port)
    conf.set("mapreduce.outputformat.class", "org.apache.hadoop.hbase.mapreduce.TableOutputFormat")
    conf.set(TableInputFormat.INPUT_TABLE, connectionConf.tableName)
    conf.set(TableOutputFormat.OUTPUT_TABLE, connectionConf.tableName)

    val table = TableName.valueOf(connectionConf.tableName)
    val connection: Connection = ConnectionFactory.createConnection(conf)
    connection.getTable(table)
  }
}
