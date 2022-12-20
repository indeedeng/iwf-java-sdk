package io.iworkflow.core;

import io.iworkflow.gen.models.RetryPolicy;
import io.iworkflow.gen.models.WorkflowStateOptions;
import org.immutables.value.Value;

import java.util.Optional;

@Value.Immutable
public abstract class WorkflowOptions {
    public abstract Integer getWorkflowTimeoutSeconds();

    public abstract Optional<io.iworkflow.gen.models.WorkflowIDReusePolicy> getWorkflowIdReusePolicy();

    public abstract Optional<String> getCronSchedule();

    public abstract Optional<WorkflowStateOptions> getStartStateOptions();

    public abstract Optional<RetryPolicy> getWorkflowRetryPolicy();

    public static WorkflowOptions minimum(final int workflowTimeoutSeconds) {
        return ImmutableWorkflowOptions.builder().workflowTimeoutSeconds(workflowTimeoutSeconds).build();
    }
}
