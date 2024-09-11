// scalastyle:off
package com.spike.spark_iceberg

import org.apache.hadoop.hbase.util.Bytes
import org.scalatest.BeforeAndAfterAll
import org.apache.spark.sql.test.SharedSparkSession

abstract class SparkTest extends SharedSparkSession with BeforeAndAfterAll {

  val columnFamily: Array[Byte] = Bytes.toBytes("cf")

}
