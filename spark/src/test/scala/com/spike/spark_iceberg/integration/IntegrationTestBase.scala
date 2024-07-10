// scalastyle:off
package com.spike.spark_iceberg.integration

import org.apache.hadoop.conf.Configuration
import org.apache.hadoop.hbase.{HBaseConfiguration, HBaseTestingUtility, HConstants}
import org.scalatest.{BeforeAndAfterAll, Sequential}

class IntegrationTestBase extends  Sequential(new EODTransactionsDataLoaderAppTest) with BeforeAndAfterAll {

  // Initialize HBaseTestingUtility
  private var hbaseTestUtil:HBaseTestingUtility = _

  override def beforeAll(): Unit = {
    super.beforeAll()

    try {
      val absolutePath = getClass
        .getResource("/hadoop/bin/winutils.exe")
        .getPath
        .replace("bin/winutils.exe", "")
      System.setProperty("hadoop.home.dir", absolutePath)
      val config: Configuration = HBaseConfiguration.create
      config.set("","")
      config.setInt(HConstants.REGIONSERVER_PORT,0)
      hbaseTestUtil = new HBaseTestingUtility(config)

      hbaseTestUtil.startMiniCluster()

      // Initialize HBase configuration
      //val hbaseConf = hbaseTestUtil.getConfiguration

     /* // Initialize HBase context
      val hbaseContext = new HBaseContext(spark.sparkContext, hbaseConf)

      // Create an in-memory HBase table
      val tableName = TableName.valueOf("test_table")
      val columnFamily = "cf"

      val connection = ConnectionFactory.createConnection(hbaseConf)
      val admin = connection.getAdmin
      if (!admin.tableExists(tableName)) {
        val tableDesc = new HTableDescriptor(tableName)
        tableDesc.addFamily(new HColumnDescriptor(columnFamily))
        admin.createTable(tableDesc)
      }*/
  }


}
