package io.iworkflow.core;

import feign.FeignException;
import io.iworkflow.core.IwfHttpException;
import io.iworkflow.core.ObjectEncoder;

// This indicates something goes wrong in the iwf application
public class ClientSideException extends IwfHttpException {
    public ClientSideException(final ObjectEncoder objectEncoder, final FeignException.FeignClientException exception) {
        super(objectEncoder, exception);
    }

    public ClientSideException(final IwfHttpException exception) {
        super(exception);
    }
}
