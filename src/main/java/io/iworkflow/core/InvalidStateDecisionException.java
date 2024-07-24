package io.iworkflow.core;

// This indicates something goes wrong in the state decision return
public class InvalidStateDecisionException extends RuntimeException {
    public InvalidStateDecisionException(Throwable cause) {
        super(cause);
    }

    public InvalidStateDecisionException(String message) {
        super(message);
    }
}
