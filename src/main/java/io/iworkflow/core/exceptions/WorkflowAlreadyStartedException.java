package io.iworkflow.core.exceptions;

import io.iworkflow.core.ClientSideException;

public class WorkflowAlreadyStartedException extends ClientSideException {
    public WorkflowAlreadyStartedException(
            final ClientSideException exception) {
        super(exception);
    }
}
