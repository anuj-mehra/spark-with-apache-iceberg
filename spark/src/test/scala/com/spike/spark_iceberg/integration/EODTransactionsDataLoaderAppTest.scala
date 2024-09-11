// scalastyle:off
package com.spike.spark_iceberg.integration

import com.spike.spark_iceberg.SparkTest
import com.spike.spark_iceberg.loader.EODTransactionsDataLoaderApp
import org.scalatest.DoNotDiscover
import org.scalatestplus.mockito.MockitoSugar

import java.nio.file.Paths

@DoNotDiscover
class EODTransactionsDataLoaderAppTest extends SparkTest with MockitoSugar {

  val projectBaseDir = Paths.get(".").toAbsolutePath.toString

 /* val obj = new EODTransactionsDataLoaderApp

  test("-----Test1----"){
    implicit val sparkSession = spark
    obj.process(sparkSession)
  }

  private def hbaseConfig(tableName): Unit ={

  }*/
}
