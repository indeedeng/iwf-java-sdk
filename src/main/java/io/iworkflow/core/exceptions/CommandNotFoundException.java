package io.iworkflow.core.exceptions;

// This indicates something goes wrong in the workflow definition
public class CommandNotFoundException extends RuntimeException {
    public CommandNotFoundException(Throwable cause) {
        super(cause);
    }

    public CommandNotFoundException(String message) {
        super(message);
    }
}
