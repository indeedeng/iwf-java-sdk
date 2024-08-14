package io.iworkflow.core.exceptions;

public class LongPollTimeoutException extends ClientSideException {
    public LongPollTimeoutException(
            final ClientSideException exception) {
        super(exception);
    }
}
