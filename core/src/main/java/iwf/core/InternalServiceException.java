package iwf.core;

// This indicates something goes wrong in the iwf-server service
public class InternalServiceException extends RuntimeException {

    public InternalServiceException(String message) {
        super(message);
    }

    public InternalServiceException(Throwable cause) {
        super(cause);
    }
}
