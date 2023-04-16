package io.iworkflow.core;

import org.immutables.value.Value;

@Value.Immutable
public abstract class ObjectExecution {
    public abstract String getObjectId();

    public abstract String getInternalRunId();
}
