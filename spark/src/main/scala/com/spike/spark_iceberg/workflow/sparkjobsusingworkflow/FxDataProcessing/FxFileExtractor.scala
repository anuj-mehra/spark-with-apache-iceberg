package com.spike.spark_iceberg.workflow.sparkjobsusingworkflow.FxDataProcessing

import com.spike.spark_iceberg.workflow.ExtractorStep
import org.apache.spark.sql.DataFrame

class FxFileExtractor extends ExtractorStep[Void, DataFrame]{

  override def process(input: Void, batchId: Long): DataFrame = {

    val respDf: DataFrame = ???

    respDf
  }


}
