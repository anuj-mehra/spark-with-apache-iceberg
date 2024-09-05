package com.spike.spark_iceberg.workflow.sparkjobsusingworkflow.FxDataProcessing

import com.spike.spark_iceberg.workflow.LoaderStep
import org.apache.spark.sql.DataFrame

class FxDataLoader extends LoaderStep[DataFrame] {

  override def process(input: DataFrame, batchId: Long): Unit = {
    // persist the dataframe here
  }
}
