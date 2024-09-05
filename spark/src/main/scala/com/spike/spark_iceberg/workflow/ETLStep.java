package com.spike.spark_iceberg.workflow;

import com.spike.spark_iceberg.workflow.exception.WorkflowContinueProcessException;
import com.spike.spark_iceberg.workflow.exception.WorkflowHaltException;

/**
 *
 * @param <I>
 * @param <O>
 */
public interface ETLStep<I,O> {

    public O process(I input, long batchId) throws WorkflowHaltException, WorkflowContinueProcessException;
}
