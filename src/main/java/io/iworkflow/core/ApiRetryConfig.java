package io.iworkflow.core;

import org.immutables.value.Value;

@Value.Immutable
public abstract class ApiRetryConfig {

    public abstract long getInitialIntervalMills();

    public abstract long getMaximumIntervalMills();

    public abstract int getMaximumAttempts();
}
