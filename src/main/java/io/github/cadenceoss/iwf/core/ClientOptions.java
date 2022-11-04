package io.github.cadenceoss.iwf.core;

import org.immutables.value.Value;

@Value.Immutable
public abstract class ClientOptions {
    public abstract String getServerUrl();

    public abstract String getWorkerUrl();

    public static final String defaultWorkerUrl = "http://localhost:8080";
    public static final String defaultServerUrl = "http://localhost:8801";

    public static final String DEFAULT_WORKER_URL_WITH_DOCKER = "http://host.docker.internal:8080";

    public static final ClientOptions localDefault = minimum(defaultWorkerUrl, defaultServerUrl);
    public static final ClientOptions localDockerDefault = minimum(DEFAULT_WORKER_URL_WITH_DOCKER, defaultServerUrl);

    public static ClientOptions minimum(final String workerUrl, final String serverUrl) {
        return ImmutableClientOptions.builder().workerUrl(workerUrl).serverUrl(serverUrl).build();
    }
}
