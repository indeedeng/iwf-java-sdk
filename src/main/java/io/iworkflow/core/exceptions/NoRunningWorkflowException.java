package io.iworkflow.core.exceptions;

import io.iworkflow.core.ClientSideException;

/**
 * A friendly named exception to indicate that the workflow does not exist or exists but not running.
 * It's the same as {@link WorkflowNotExistsException} but with a different name.
 * It's subclass of {@link ClientSideException} with ErrorSubStatus.WORKFLOW_NOT_EXISTS_SUB_STATUS
 */
public class NoRunningWorkflowException extends WorkflowNotExistsException {
    public NoRunningWorkflowException(
            final ClientSideException exception) {
        super(exception);
    }
}
