package io.iworkflow.core.exceptions;

import io.iworkflow.core.ClientSideException;

/**
 * A friendly named exception to indicate that the workflow does not exist
 * It's subclass of {@link ClientSideException} with ErrorSubStatus.WORKFLOW_NOT_EXISTS_SUB_STATUS
 */
public class WorkflowNotExistsException extends ClientSideException {
    public WorkflowNotExistsException(
            final ClientSideException exception) {
        super(exception);
    }
}
