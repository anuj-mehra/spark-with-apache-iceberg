// scalastyle:off
package com.spike.spark_iceberg.integration

import com.spike.spark_iceberg.SparkTest
import com.spike.spark_iceberg.loader.EODTransactionsDataLoaderApp
import com.spike.spark_iceberg.repository.hbase.{HBaseReadRepository, HBaseWriteRepository}
import org.apache.hadoop.hbase.{HBaseTestingUtility, TableExistsException, TableName}
import org.scalatest.DoNotDiscover
import org.scalatestplus.mockito.MockitoSugar

import java.nio.file.Paths

@DoNotDiscover
class EODTransactionsDataLoaderAppTest extends SparkTest with MockitoSugar {

  val projectBaseDir = Paths.get(".").toAbsolutePath.toString

  private implicit val hbaseTestUtility: HBaseTestingUtility = HBaseTestUtility.getPreCreatedHBaseTestingUtility
  var appConfPath: String = _

  val tranTableName: TableName = TableName.valueOf("cpbnd:tran")
  val tranIndexTableName: TableName = TableName.valueOf("cpbnd:tranIndex")

  override protected def beforeAll(): Unit = {
    super.beforeAll()
    this.createTable(tranTableName, hbaseTestUtility)
    this.createTable(tranIndexTableName, hbaseTestUtility)

  }

  test("-----Test1----"){
    implicit val sparkSession = spark

    val hbaseReadRepo = HBaseReadRepository("cpbnd:tran", hbaseTestUtility.getConfiguration)(spark)
    val hbaseWriteRepo = HBaseWriteRepository("cpbnd:tranIndex", hbaseTestUtility.getConfiguration)
    val obj = new EODTransactionsDataLoaderApp(hbaseReadRepo, hbaseWriteRepo)
    obj.process(sparkSession)
  }

  protected def createTable(tableName: TableName, hbaseTestUtility:HBaseTestingUtility): Unit = {
    try{
      hbaseTestUtility.createTable(tableName, columnFamily)
    }catch{
      case e: TableExistsException =>
        println("-----table already exists----" + tableName.getName)
      case e: Exception =>
        throw e
    }

  }

}
