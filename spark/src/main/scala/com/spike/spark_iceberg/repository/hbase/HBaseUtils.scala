package com.spike.spark_iceberg.repository.hbase

import com.typesafe.scalalogging.LazyLogging
import org.apache.hadoop.hbase.client.{Delete, Put}
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

}
