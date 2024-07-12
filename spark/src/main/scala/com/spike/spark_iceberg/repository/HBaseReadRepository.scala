// scalastyle:off
package com.spike.spark_iceberg.repository

import com.typesafe.scalalogging.LazyLogging
import org.apache.hadoop.conf.Configuration
import org.apache.spark.sql.DataFrame

class HBaseReadRepository(@transient conf: Configuration) extends Serializable with LazyLogging{

  def putAll(inputDf: DataFrame, rowKeyColumnName: String): Unit ={

  }

}
object HBaseReadRepository {

}
