package io.iworkflow.core;

// This indicates something goes wrong in the workflow definition
public class WorkflowDefinitionException extends RuntimeException {
    public WorkflowDefinitionException(Throwable cause) {
        super(cause);
    }

    public WorkflowDefinitionException(String message) {
        super(message);
    }
}
