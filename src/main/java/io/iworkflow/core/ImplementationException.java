package io.iworkflow.core;

// This indicates issues in the implementation of the workflow
public class ImplementationException extends RuntimeException {
    public ImplementationException(Throwable cause) {
        super(cause);
    }

    public ImplementationException(String message) {
        super(message);
    }
}
