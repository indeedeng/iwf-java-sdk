package io.iworkflow.core.exceptions;

public class WorkflowAlreadyStartedException extends ClientSideException {
    public WorkflowAlreadyStartedException(
            final ClientSideException exception) {
        super(exception);
    }
}
