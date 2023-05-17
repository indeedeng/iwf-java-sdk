package io.iworkflow.core;

import feign.FeignException;
import feign.Response;
import feign.RetryableException;
import feign.codec.ErrorDecoder;

import java.util.Date;

import static feign.FeignException.errorStatus;

class ServerErrorRetryDecoder implements ErrorDecoder {

    @Override
    public Exception decode(String methodKey, Response response) {
        if (response.status() >= 500 && response.status()<600) {

            FeignException exception = errorStatus(methodKey, response);
                return new RetryableException(
                        response.status(),
                        exception.getMessage(),
                        response.request().httpMethod(),
                        exception,
                        new Date(),
                        response.request());
        }
        return new ErrorDecoder.Default().decode(methodKey, response);
    }
}