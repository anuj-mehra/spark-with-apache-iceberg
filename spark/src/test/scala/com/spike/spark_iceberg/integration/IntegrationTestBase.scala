// scalastyle:off
package com.spike.spark_iceberg.integration

import org.apache.hadoop.conf.Configuration
import org.apache.hadoop.hbase.{HBaseConfiguration, HBaseTestingUtility, HConstants}
import org.scalatest.{BeforeAndAfterAll, Sequential}

import java.nio.file.Paths

class IntegrationTestBase extends  Sequential(new EODTransactionsDataLoaderAppTest) with BeforeAndAfterAll {

  // Initialize HBaseTestingUtility
  private var hbaseTestUtil:HBaseTestingUtility = _

  override def beforeAll(): Unit = {
    super.beforeAll()
    val projectBaseDir = Paths.get("").toAbsolutePath.toString
    println("projectBaseDir==>" + projectBaseDir)

    try {
      println("absolutePath==>" + s"${projectBaseDir}/spark/src/test/resources/hadoop.bin/winutils.exe")

      System.setProperty("hadoop.home.dir", s"${projectBaseDir}/spark/src/test/resources/hadoop.bin/")

      /*val config: Configuration = HBaseConfiguration.create
      config.set("mapreduce.outputformat.class", "org.apache.hadoop.hbase.mapreduce.TableOutputFormat")
      config.setInt(HConstants.REGIONSERVER_PORT, 0)
      hbaseTestUtil = new HBaseTestingUtility(config)*/
      hbaseTestUtil = new HBaseTestingUtility()
      hbaseTestUtil.startMiniCluster()

    }
  }

  override def afterAll(): Unit = {
    super.afterAll()
    hbaseTestUtil.shutdownMiniCluster()
  }

}
