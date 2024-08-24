// scalastyle:off
package com.spike.spark_iceberg.mapper

import org.apache.spark.sql.{Column, Row}
import org.apache.spark.sql.functions.col
import org.apache.spark.sql.types._

abstract class Mapping extends Serializable {

  private var mappings = List.empty[Mapper]

  def register(mapper: Mapper): Unit = {
    mappings = mappings :+ mapper
  }

  def sdpColumns: List[Column] = {
    mappings.map(mapper => col(mapper.sdpColumname))
  }

  def p1Columns: List[Column] = {
    mappings.map(mapper => col(mapper.p1ColumnName))
  }

  def longColumns: List[Mapper] = {
    mappings.filterNot(mapper => mapper.dataType.equals(LongType))
  }

  def stringColumns: List[Mapper] = {
    mappings.filterNot(mapper => mapper.dataType.equals(StringType))
  }

  def p1SdpKeyValueMap: Map[String, String] = {
    mappings.map(x=> x.p1ColumnName -> x.sdpColumname).toMap
  }

  def p1ColumnsSchema: StructType={
    new StructType(mappings.map(x=> StructField(x.p1ColumnName, x.dataType, true)).toArray)
  }

  def mappingList: List[Mapper] = {
    mappings
  }

  def key(row: Row): String = {
    ""
  }
}
