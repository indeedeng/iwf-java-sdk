package io.iworkflow.core;

// This indicates something goes wrong in the object definition
public class ObjectDefinitionException extends RuntimeException {
    public ObjectDefinitionException(Throwable cause) {
        super(cause);
    }

    public ObjectDefinitionException(String message) {
        super(message);
    }
}
