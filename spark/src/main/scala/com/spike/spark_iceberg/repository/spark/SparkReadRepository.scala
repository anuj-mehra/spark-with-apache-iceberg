// scalastyle:off
package com.spike.spark_iceberg.repository.spark

import com.spike.spark_iceberg.ingestionframework.fileingestion.Constants
import com.typesafe.scalalogging.LazyLogging
import org.apache.spark.rdd.RDD
import org.apache.spark.sql.functions.{col, explode}
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

  def readCsv(filePath: String, delimiter: String = ","): DataFrame = {
    sparkSession.read.format(Constants.CSV_FORMAT)
      .option("header", "true")
      .option("sep", delimiter)
      .option("inferSchema", "true")
      .load(filePath)
  }

  def readTextFile(filePath: String, lineSeparator: String, recordDelimiter: String): RDD[String] = {

    val rdd: RDD[String] = lineSeparator match {
      case flag if (flag == null || flag.isEmpty) => sparkSession.sparkContext.textFile(filePath)
      case _ =>
        val lineSep = if(recordDelimiter == "\\n")
          Constants.LINE_SEPARATOR_LF
        else if(recordDelimiter == "\\r")
          Constants.LINE_SEPARATOR_CR
        else if(recordDelimiter == "\\r\\n")
          Constants.LINE_SEPARATOR_CR_LF
        else
          recordDelimiter

        val df = sparkSession.read.format("text").option("charset","UTF-8").option("lineSep", lineSep).load(filePath)
        df.rdd.map(row => row.getString(0))
    }

    rdd
  }

  def readXmlFile(filePath: String, xsdFilePath: String, rowTagColName: String, explodeColName: String, envName: String): DataFrame = {

    sparkSession.read
      .format("com.databricks.spark.xml")
      .option("rowTag", rowTagColName)
      .option("rowValidationXSDPath", xsdFilePath)
      .option("inferSchema", false)
      .option("ignoreNamespace", "true")
      .option("excludeAttribute", "false")
      .option("mode", "FAILFAST")
      .option("columnNameOfCorruptRecord", "_malformed_records")
      .load(filePath)
      .withColumn(explodeColName, explode(col(explodeColName)))

  }

  def ebcdicFileReader(filePath: String, copyBookFilePath: String): DataFrame = {
    sparkSession
      .read
      .format("za.co.absa.cobrix.spark.cobol.source")
      .option("copybook", copyBookFilePath)
      .option("schema_retention_policy", "collapse_root")
      .option("generate_record_id", true)
      .option("string_trimming_policy", "none")
      .load(filePath)
  }

  def ebcdicFileReaderAsStr(filePath: String, copyBookFilePath: String): DataFrame = {
    sparkSession
      .read
      .format("za.co.absa.cobrix.spark.cobol.source")
      .option("copybook", copyBookFilePath)
      .option("schema_retention_policy", "collapse_root")
      .option("generate_record_id", true)
      .option("string_trimming_policy", "none")
      .load(filePath)
  }

  def readEbcdicFixedWidthFile(filePath: String, copyBookFilePath: String, recordsPartition: String): DataFrame = {
    sparkSession
      .read
      .format("za.co.absa.cobrix.spark.cobol.source")
      .option("copybook", copyBookFilePath)
      .option("schema_retention_policy", "collapse_root")
      .option("encoding", "ascii")
      .option("generate_record_id", true)
      .option("string_trimming_policy", "none")
      .option("input_split_records", recordsPartition)
      .load(filePath)
  }

  def readEbcdicVariableWidthFile(filePath: String, copyBookFilePath: String): DataFrame = {
    sparkSession
      .read
      .format("za.co.absa.cobrix.spark.cobol.source")
      .option("copybook", copyBookFilePath)
      .option("schema_retention_policy", "collapse_root")
      .option("generate_record_id", true)
      .option("string_trimming_policy", "none")
      .option("variable_size_occurs", "true")
      .load(filePath)
  }


}
object SparkReadRepository {

  def apply(sparkSession: SparkSession) = new SparkReadRepository(sparkSession)

}
