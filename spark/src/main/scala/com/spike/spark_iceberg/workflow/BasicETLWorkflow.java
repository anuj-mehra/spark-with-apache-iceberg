package com.spike.spark_iceberg.workflow;

import com.spike.spark_iceberg.workflow.exception.WorkflowContinueProcessException;
import com.spike.spark_iceberg.workflow.exception.WorkflowHaltException;

import java.util.ArrayList;
import java.util.List;

public class BasicETLWorkflow implements ETLWorkflow{

    private final ExtractorStep<Void, ?> inputSource;

    private final List<ETLStep<?,?>> steps;

    private BasicETLWorkflow(final ExtractorStep<Void , ?> inputSource){
        this.inputSource = inputSource;
        this.steps = new ArrayList<>();
    }

    private long batchId;

    public final void execute() throws WorkflowHaltException, WorkflowContinueProcessException{

        Object output = null;

        if(null != inputSource){
            try{
                output = inputSource.process(null, batchId);
            }catch(WorkflowHaltException e){
                throw new WorkflowHaltException(e.getLocalizedMessage(), e);
            }
        }

        for(final ETLStep etlStep: steps){

            if(etlStep instanceof TransformationStep<?,?>){
                final TransformationStep transformationStep = (TransformationStep<?,?>) etlStep;
                output = transformationStep.process(output, batchId);
            }
            else if(etlStep instanceof LoaderStep<?>){
                final LoaderStep loaderStep = (LoaderStep<?>)etlStep;
                loaderStep.process(output, batchId);
            }
        }
    }

    public static class ETLWorkflowBuilder {

        private final BasicETLWorkflow workflow;

        public ETLWorkflowBuilder(final ExtractorStep<Void, ?> input, final long batchId){
            this.workflow = new BasicETLWorkflow(input);
            this.workflow.batchId = batchId;
        }

        public ETLWorkflowBuilder nextStep(final ETLStep<?,?> step){
            this.workflow.steps.add(step);
            return this;
        }

        public BasicETLWorkflow build(){
            return workflow;
        }
    }
}
