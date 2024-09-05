// scalastyle:off
package com.spike.spark_iceberg.workflow.sparkjobsusingworkflow.FxDataProcessing

import com.spike.spark_iceberg.workflow.exception.{WorkflowContinueProcessException, WorkflowHaltException}
import com.spike.spark_iceberg.workflow.{BasicETLWorkflow, ETLWorkflow}

object FxDataProcessWorkflow extends App{

  val extractorStep = new FxFileExtractor
  val transformerStep = new FxDataTransformer
  val loaderStep = new FxDataLoader

  val batchId = 100L


  try{
    val workflow: ETLWorkflow = new BasicETLWorkflow
    .ETLWorkflowBuilder(extractorStep, batchId)
      .nextStep(transformerStep)
      .nextStep(loaderStep)
      .build()

    workflow.execute()
  }catch{
    case e: WorkflowHaltException =>
      false
    case e: WorkflowContinueProcessException =>
      true
  }



}
