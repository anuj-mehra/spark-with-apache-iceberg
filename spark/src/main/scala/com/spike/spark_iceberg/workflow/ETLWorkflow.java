package com.spike.spark_iceberg.workflow;

import com.spike.spark_iceberg.workflow.exception.WorkflowContinueProcessException;
import com.spike.spark_iceberg.workflow.exception.WorkflowHaltException;

public interface ETLWorkflow {

    public void execute() throws WorkflowHaltException, WorkflowContinueProcessException;
}
