package io.iworkflow.core;

// This indicates the client is not fully initialized
public class ClientNotFullyInitializedException extends RuntimeException {
    public ClientNotFullyInitializedException(final String message) {
        super(message);
    }
}
