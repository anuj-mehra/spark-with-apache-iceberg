// scalastyle:off
package com.spike.spark_iceberg.utils

import org.apache.spark.sql.DataFrame

object GetOptimalPartitions {

  private lazy val DEFAULT_BLOCK_SIZE = "256"

  def getOptimalDataFramePartitions(inputDf: DataFrame): Int = {

    val blockSize = DEFAULT_BLOCK_SIZE
    var noOfPartfiles: Int = 1

    if(inputDf.head(1).isEmpty){
      noOfPartfiles = 1
    }else{
      val bytes = inputDf.rdd.map(_.toString()).map(_.getBytes("UTF-8").length.toLong).reduce(_ + _)
      val actualSize = (bytes/(1024*1024)).toInt
      if(actualSize > blockSize.toInt){
        noOfPartfiles = actualSize/blockSize.toInt
      }
    }

    noOfPartfiles
  }

}
