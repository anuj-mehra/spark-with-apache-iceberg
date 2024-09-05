package com.spike.spark_iceberg.workflow.sparkjobsusingworkflow.FxDataProcessing

import com.spike.spark_iceberg.workflow.TransformationStep
import org.apache.spark.sql.DataFrame

class FxDataTransformer extends TransformationStep[DataFrame, DataFrame] {
  override def process(input: DataFrame, batchId: Long): DataFrame = {

    val transformedDf = input
    transformedDf
  }
}
