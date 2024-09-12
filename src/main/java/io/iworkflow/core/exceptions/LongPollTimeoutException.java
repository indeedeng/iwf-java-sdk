package io.iworkflow.core.exceptions;

import io.iworkflow.core.ClientSideException;

public class LongPollTimeoutException extends ClientSideException {
    public LongPollTimeoutException(
            final ClientSideException exception) {
        super(exception);
    }
}
