package io.iworkflow.core;

import feign.FeignException;

// This indicates something goes wrong in the iwf-server service
public class ServerSideException extends IwfHttpException {
    public ServerSideException(final ObjectEncoder objectEncoder, final FeignException.FeignClientException exception) {
        super(objectEncoder, exception);
    }
}
