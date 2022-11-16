package io.github.cadenceoss.iwf.core;

import io.github.cadenceoss.iwf.core.options.WorkflowIdReusePolicy;
import org.immutables.value.Value;

import javax.annotation.Nullable;

@Value.Immutable
public abstract class WorkflowStartOptions {
    public abstract Integer getWorkflowTimeoutSeconds();

    @Nullable
    public abstract WorkflowIdReusePolicy getWorkflowIdReusePolicy();

    @Nullable
    public abstract String getCronSchedule();

    public static WorkflowStartOptions minimum(final int workflowTimeoutSeconds) {
        return ImmutableWorkflowStartOptions.builder().workflowTimeoutSeconds(workflowTimeoutSeconds).build();
    }
}
