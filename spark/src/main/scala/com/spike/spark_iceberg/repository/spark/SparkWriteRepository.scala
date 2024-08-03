// scalastyle:off
package com.spike.spark_iceberg.repository.spark

import com.typesafe.scalalogging.LazyLogging
import org.apache.spark.sql.{DataFrame, SaveMode}

class SparkWriteRepository extends Serializable with LazyLogging {

  private lazy val DEFAULT_BLOCK_SIZE = "256"

  def writeCSV(inputDf: DataFrame, path: String, headerFlag: Boolean=false): Unit = {

    val dfw = inputDf.coalesce(1).write.mode(SaveMode.Overwrite)

    headerFlag match {
      case true => dfw.option("header", "true").csv(path)
      case false => dfw.csv(path)
    }
  }

  def writeParquet(inputDf: DataFrame, path: String): Unit = {
      inputDf.write.mode(SaveMode.Overwrite).parquet(path)
  }

  def writeParquetOptimizedPartitions(inputDf: DataFrame, path: String): Unit = {
    val numOfPartitions = this.optimizedNumberOfPartitions(inputDf)
    inputDf.repartition(numOfPartitions).write.mode(SaveMode.Overwrite).parquet(path)

  }

  def writeCSVFile(inputDf: DataFrame, path: String): Unit = {


  }
  private def optimizedNumberOfPartitions(df: DataFrame): Int = {
    val blockSize = DEFAULT_BLOCK_SIZE
    var noOfPartfiles: Int = 1

    if(df.head(1).isEmpty){
        noOfPartfiles = 1
    }else{
      val bytes = df.rdd.map(_.toString()).map(_.getBytes("UTF-8").length.toLong).reduce(_ + _)
      val actualSize = (bytes/(1024*1024)).toInt
      if(actualSize > blockSize.toInt){
        noOfPartfiles = actualSize/blockSize.toInt
      }
    }

    noOfPartfiles
  }


}
object SparkWriteRepository extends Serializable{

  def apply = new SparkWriteRepository
}
