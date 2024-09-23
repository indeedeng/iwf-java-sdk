package io.iworkflow.core.exceptions;

import io.iworkflow.core.WorkflowDefinitionException;

// This indicates something goes wrong in the workflow definition
public class CommandNotFoundException extends WorkflowDefinitionException {
    public CommandNotFoundException(Throwable cause) {
        super(cause);
    }

    public CommandNotFoundException(String message) {
        super(message);
    }
}
