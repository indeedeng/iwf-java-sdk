package io.iworkflow.core.exceptions;

import io.iworkflow.core.ClientSideException;

public class NoRunningWorkflowException extends WorkflowNotExistsOrOpenException {
    public NoRunningWorkflowException(
            final ClientSideException exception) {
        super(exception);
    }
}
