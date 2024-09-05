package com.spike.spark_iceberg.workflow.exception;

public class WorkflowContinueProcessException extends Exception{

    private static final long serialVersionUID = 1L;

    public WorkflowContinueProcessException(){
        super();
    }

    public WorkflowContinueProcessException(String message){
        super(message);
    }

    public WorkflowContinueProcessException(String message, Throwable cause){
        super(message,cause);
    }

    public WorkflowContinueProcessException(Throwable cause){
        super(cause);
    }

    public WorkflowContinueProcessException(String message, Throwable cause,
                                 boolean enableSuppression,
                                 boolean writableStacktrace){
        super(message,cause,enableSuppression,writableStacktrace);
    }
}
