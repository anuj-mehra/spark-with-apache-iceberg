package com.spike.spark_iceberg.ingestionframework.fileingestion

object Constants {

  val FIXED_WIDTH_FILE_TYPE = "fixed_width"
  val DELIMITED_FILE_TYPE = "demilited"
  val HYBRID = "hybrid"

  val LINE_SEPARATOR_LF = "\n"
  val LINE_SEPARATOR_CR = "\r"
  val LINE_SEPARATOR_CR_LF = "\r\n"

  val AVRO_FORMAT = "avro"
  val CSV_FORMAT = "csv"
  val PARQUET_FORMAT = "parquet"
  val HIVE_FORMAT = "hive"

  val AVRO_SCHEMA = "avroSchema"

}
