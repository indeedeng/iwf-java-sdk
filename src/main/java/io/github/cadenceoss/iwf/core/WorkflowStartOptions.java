package io.github.cadenceoss.iwf.core;

import io.github.cadenceoss.iwf.core.options.WorkflowIdReusePolicy;
import org.immutables.value.Value;

@Value.Immutable
public abstract class WorkflowStartOptions {
    public abstract Integer getWorkflowTimeoutSeconds();
    public abstract WorkflowIdReusePolicy getWorkflowIdReusePolicy();
    public abstract String getCronSchedule();

    public static WorkflowStartOptions minimum(final int workflowTimeoutSeconds) {
        return ImmutableWorkflowStartOptions.builder().workflowTimeoutSeconds(workflowTimeoutSeconds).build();
    }

    public static WorkflowStartOptions getDefault() {
        return ImmutableWorkflowStartOptions.builder()
                .workflowIdReusePolicy(WorkflowIdReusePolicy.ALLOW_DUPLICATE_FAILED_ONLY)
                .build();
    }
}
