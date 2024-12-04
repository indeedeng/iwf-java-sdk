package io.iworkflow.core;

import org.immutables.value.Value;

import java.util.Map;
import java.util.Optional;

import static java.util.concurrent.TimeUnit.SECONDS;

@Value.Immutable
public abstract class ClientOptions {
    public abstract String getServerUrl();

    public abstract String getWorkerUrl();

    public abstract ObjectEncoder getObjectEncoder();

    public abstract Optional<Integer> getLongPollApiMaxWaitTimeSeconds();

    public abstract Map<String,String> getRequestHeaders();

    @Value.Default
    public ServiceApiRetryConfig getServiceApiRetryConfig() {
        return ImmutableServiceApiRetryConfig.builder()
                .initialIntervalMills(100)
                .maximumIntervalMills(SECONDS.toMillis(1))
                .maximumAttempts(10)
                .build();
    }
    //Use "http://localhost:8080" when using iwf-java-sdk as the worker (src/test/java/io/iworkflow/spring/SpringMainApplication.java)
    public static final String defaultWorkerUrl = "http://localhost:8802";

    public static final String workerUrlFromDocker = "http://host.docker.internal:8802";
    public static final String defaultServerUrl = "http://localhost:8801";

    public static final ClientOptions localDefault = minimum(defaultWorkerUrl, defaultServerUrl);

    // use this when running with docker-compose of iWF server
    public static final ClientOptions dockerDefault = minimum(workerUrlFromDocker, defaultServerUrl);


    public static ClientOptions minimum(final String workerUrl, final String serverUrl) {
        return ImmutableClientOptions.builder()
                .workerUrl(workerUrl)
                .serverUrl(serverUrl)
                .objectEncoder(new JacksonJsonObjectEncoder())
                .build();
    }

    public static ImmutableClientOptions.Builder builder() {
        return ImmutableClientOptions.builder();
    }
}
