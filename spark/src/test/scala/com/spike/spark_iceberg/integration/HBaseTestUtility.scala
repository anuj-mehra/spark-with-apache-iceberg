// scalastyle:off
package com.spike.spark_iceberg.integration

import org.apache.hadoop.conf.Configuration
import org.apache.hadoop.hbase.{HBaseConfiguration, HBaseTestingUtility, HConstants}

import java.nio.file.Paths

object HBaseTestUtility {

  var hbaseTestUtility: Option[HBaseTestingUtility] = None

  private def getHBaseTestUtility: HBaseTestingUtility = {


     val projectBaseDir = Paths.get("").toAbsolutePath.toString
    println("projectBaseDir==>" + projectBaseDir)
     println("absolutePath==>" + s"${projectBaseDir}/spark/src/test/resources/hadoop.bin/winutils.exe")

      System.setProperty("hadoop.home.dir", s"${projectBaseDir}/spark/src/test/resources/hadoop.bin/")

    /*val absolutePath = getClass
      .getResource("/hadoop/bin/winutils.exe")
      .getPath
      .replace("bin/winutils.exe", "")

    System.setProperty("hadoop.home.dir", absolutePath)*/

    val config: Configuration = HBaseConfiguration.create
    config.set("mapreduce.outputformat.class", "org.apache.hadoop.hbase.mapreduce.TableOutputFormat")
    config.setInt(HConstants.REGIONSERVER_PORT, 0)
    new HBaseTestingUtility(config)
  }


  def createHBaseTestingUtility: HBaseTestingUtility = {
    hbaseTestUtility = Some(this.getHBaseTestUtility)
    hbaseTestUtility.get
  }

  def getPreCreatedHBaseTestingUtility: HBaseTestingUtility = {
    hbaseTestUtility match {
      case Some(_) =>
        hbaseTestUtility.get
      case None =>
        this.getHBaseTestUtility
    }
  }
}
