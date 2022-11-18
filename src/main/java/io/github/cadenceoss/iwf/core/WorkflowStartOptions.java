package io.github.cadenceoss.iwf.core;

import io.github.cadenceoss.iwf.core.options.WorkflowIdReusePolicy;
import org.immutables.value.Value;

import javax.annotation.Nullable;
import java.util.Optional;

@Value.Immutable
public abstract class WorkflowStartOptions {
    public abstract Integer getWorkflowTimeoutSeconds();

    public abstract Optional<WorkflowIdReusePolicy> getWorkflowIdReusePolicy();

    public abstract Optional<String> getCronSchedule();

    public static WorkflowStartOptions minimum(final int workflowTimeoutSeconds) {
        return ImmutableWorkflowStartOptions.builder().workflowTimeoutSeconds(workflowTimeoutSeconds).build();
    }
}
