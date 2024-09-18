package io.iworkflow.core.exceptions;

import io.iworkflow.core.ClientSideException;

/**
 * @deprecated Use NoRunningWorkflowException instead
 */
@Deprecated
public class WorkflowNotExistsOrOpenException extends ClientSideException {
    public WorkflowNotExistsOrOpenException(
            final ClientSideException exception) {
        super(exception);
    }
}
