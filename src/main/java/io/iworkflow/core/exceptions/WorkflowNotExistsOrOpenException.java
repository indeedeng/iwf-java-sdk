package io.iworkflow.core.exceptions;

public class WorkflowNotExistsOrOpenException extends ClientSideException {
    public WorkflowNotExistsOrOpenException(
            final ClientSideException exception) {
        super(exception);
    }
}
