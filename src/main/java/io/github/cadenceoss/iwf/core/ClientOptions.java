package io.github.cadenceoss.iwf.core;

import org.immutables.value.Value;

@Value.Immutable
public abstract class ClientOptions {
    public abstract String getServerUrl();

    public abstract String getWorkerUrl();

    public static final String defaultWorkerUrl = "http://localhost:8802";

    public static final String workerUrlFromDocker = "http://host.docker.internal:8802";
    public static final String defaultServerUrl = "http://localhost:8801";

    public static final ClientOptions localDefault = minimum(defaultWorkerUrl, defaultServerUrl);

    // use this when running with docker-compose of iWF server
    public static final ClientOptions dockerDefault = minimum(workerUrlFromDocker, defaultServerUrl);

    public static ClientOptions minimum(final String workerUrl, final String serverUrl) {
        return ImmutableClientOptions.builder().workerUrl(workerUrl).serverUrl(serverUrl).build();
    }
}
