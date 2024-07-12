// scalastyle:off
package com.spike.spark_iceberg.repository.hbase

import org.apache.hadoop.conf.Configuration
import org.apache.hadoop.hbase.mapreduce.TableInputFormat
import org.apache.spark.broadcast.Broadcast
import org.apache.spark.sql.SparkSession

case class HBaseConnectionConfig(quorum: String, port: Int, tableName: String) extends Serializable

object HBaseConnectionConfig extends Serializable{

  val ZookeeperQuorum = "hbase.zookeeper.quorum"
  val ZookeeperPort = "hbase.zookeeper.property.clientPort"
  val TableName = TableInputFormat.INPUT_TABLE

  def apply(sparkSession: SparkSession, conf: Configuration): Broadcast[HBaseConnectionConfig] = {
    val quorum = conf.get(ZookeeperQuorum)
    val port = conf.get(ZookeeperPort).toInt
    val tableName = conf.get(TableName)

    sparkSession.sparkContext.broadcast(HBaseConnectionConfig(quorum,port,tableName))
  }

}
