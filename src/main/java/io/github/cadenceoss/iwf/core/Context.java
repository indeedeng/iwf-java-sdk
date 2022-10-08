package io.github.cadenceoss.iwf.core;

import org.immutables.value.Value;

@Value.Immutable
public abstract class Context {
    public abstract Integer getWorkflowStartTimestampSeconds();

    public abstract String getStateExecutionId();

    public abstract String getWorkflowRunId();

    public abstract String getWorkflowId();
}
