package io.github.cadenceoss.iwf.core;

import org.immutables.value.Value;

@Value.Immutable
public abstract class WorkerOptions {

    public abstract ObjectEncoder getObjectEncoder();

    // use this when running with docker-compose of iWF server
    public static final WorkerOptions defaultOptions = minimum(new JacksonJsonObjectEncoder());

    public static WorkerOptions minimum(final ObjectEncoder objectEncoder) {
        return ImmutableWorkerOptions.builder()
                .objectEncoder(objectEncoder)
                .build();
    }

    public static ImmutableWorkerOptions.Builder builder() {
        return ImmutableWorkerOptions.builder();
    }
}
