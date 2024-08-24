// scalastyle:off
package com.spike.spark_iceberg.mapper

import org.apache.spark.sql.types.DataType

case class Mapper(sdpColumname: String, p1ColumnName: String, dataType: DataType,
                  isNumeric: Boolean = false) extends Serializable

object Mapper {

  def apply(columnName: String, dataType: DataType): Mapper = new Mapper(columnName, columnName, dataType)
}
