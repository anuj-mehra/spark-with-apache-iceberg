// scalastyle:off
package com.spike.spark_iceberg.repository.hive

import com.typesafe.scalalogging.LazyLogging
import org.apache.hadoop.conf.Configuration
import org.apache.hadoop.fs.{FileSystem, Path}
import org.apache.spark.sql.DataFrame
import org.apache.spark.sql.functions.col

class HiveWriteRepository extends Serializable  with LazyLogging{

  val PATH_SEPARATOR = "/"

  def writeIntoHiveTable(inputDf: DataFrame, partitionColArrays: Array[String],
                         hiveTableBasePath: String,
                         hiveDbName:String, hiveTableName: String): Unit = {

    val configuration: Configuration = inputDf.sparkSession.sparkContext.hadoopConfiguration
    val fs: FileSystem = FileSystem.get(configuration)
    try{
      val partitionedColsDf = inputDf.select(partitionColArrays.map(c => col(c)):_*).distinct()
      partitionedColsDf.collect().foreach{ row =>

        val partitionClause = partitionColArrays.map(colName => s"${colName}=${row.getAs[Any](colName)}")
        val partitionPath = partitionClause.mkString(PATH_SEPARATOR) + PATH_SEPARATOR
        val fullHdfsPath = hiveTableBasePath + PATH_SEPARATOR + partitionPath

        try{
          if(fs.exists(new Path(fullHdfsPath))){
            val deleteStatus = fs.delete(new Path(fullHdfsPath), true)
            if(deleteStatus){
              println(s"-----delete success----${fullHdfsPath}")
            }else{
              println(s"-----delete failure----${fullHdfsPath}")
            }
          }
        }catch{
          case e: Exception => throw e
        }
      }
    }finally{
      fs.close()
    }

    val hiveTable = hiveDbName.concat(".").concat(hiveTableName);
    val hiveTableRepair = s"""MSCK REPAIR TABLE ${hiveTable}"""
    inputDf.sparkSession.sql(hiveTableRepair)

    inputDf.write.partitionBy(partitionColArrays: _*).mode("append").format("hive").saveAsTable(hiveTable)

  }
}
