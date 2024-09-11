// scalastyle:off
package com.spike.spark_iceberg.integration

import org.apache.hadoop.conf.Configuration
import org.apache.hadoop.hbase.{HBaseConfiguration, HBaseTestingUtility, HConstants, NamespaceDescriptor}
import org.scalatest.{BeforeAndAfterAll, Sequential}
import org.apache.log4j.Level

import java.nio.file.Paths

class IntegrationTestBase extends  Sequential(new EODTransactionsDataLoaderAppTest) with BeforeAndAfterAll {

  // Initialize HBaseTestingUtility
  private var hbaseTestUtility:HBaseTestingUtility = _

  override def beforeAll(): Unit = {
    org.apache.log4j.Logger.getRootLogger.setLevel(Level.ERROR)
    super.beforeAll()
    HBaseTestUtility.hbaseTestUtility match {
      case None =>
        hbaseTestUtility = HBaseTestUtility.createHBaseTestingUtility
      case Some(_) =>
        hbaseTestUtility = HBaseTestUtility.hbaseTestUtility.get
    }
    try{
      hbaseTestUtility.startMiniCluster()
      hbaseTestUtility.getConnection.getAdmin.createNamespace(NamespaceDescriptor.create("cpbnd").build())
    }catch{
      case e:Exception =>
        e.printStackTrace
        throw e
    }


    /*val projectBaseDir = Paths.get("").toAbsolutePath.toString
    println("projectBaseDir==>" + projectBaseDir)

    try {
      println("absolutePath==>" + s"${projectBaseDir}/spark/src/test/resources/hadoop.bin/winutils.exe")

      System.setProperty("hadoop.home.dir", s"${projectBaseDir}/spark/src/test/resources/hadoop.bin/")

      val config: Configuration = HBaseConfiguration.create
      config.set("mapreduce.outputformat.class", "org.apache.hadoop.hbase.mapreduce.TableOutputFormat")
      config.setInt(HConstants.REGIONSERVER_PORT, 0)
      hbaseTestUtil = new HBaseTestingUtility(config)
      //hbaseTestUtil = new HBaseTestingUtility()
      hbaseTestUtil.startMiniCluster()

    }*/


  }

  override def afterAll(): Unit = {
    super.afterAll()
    hbaseTestUtility.shutdownMiniCluster()
  }

}
