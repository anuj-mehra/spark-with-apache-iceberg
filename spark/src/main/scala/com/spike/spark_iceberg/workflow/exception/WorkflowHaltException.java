package com.spike.spark_iceberg.workflow.exception;

public class WorkflowHaltException extends Exception{

    private static final long serialVersionUID = 1L;

    public WorkflowHaltException(){
        super();
    }

    public WorkflowHaltException(String message){
        super(message);
    }

    public WorkflowHaltException(String message, Throwable cause){
        super(message,cause);
    }

    public WorkflowHaltException(Throwable cause){
        super(cause);
    }

    public WorkflowHaltException(String message, Throwable cause,
                                 boolean enableSuppression,
                                 boolean writableStacktrace){
        super(message,cause,enableSuppression,writableStacktrace);
    }
}
