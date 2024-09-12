package io.iworkflow.core.exceptions;

import io.iworkflow.core.ClientSideException;

public class WorkflowNotExistsOrOpenException extends ClientSideException {
    public WorkflowNotExistsOrOpenException(
            final ClientSideException exception) {
        super(exception);
    }
}
