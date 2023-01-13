package io.iworkflow.core;

import feign.FeignException;

// This indicates something goes wrong in the iwf application
public class ClientSideException extends IwfHttpException {
    public ClientSideException(final ObjectEncoder objectEncoder, final FeignException.FeignClientException exception) {
        super(objectEncoder, exception);
    }
}
