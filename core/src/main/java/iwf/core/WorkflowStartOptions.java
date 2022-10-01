package iwf.core;

import org.immutables.value.Value;

@Value.Immutable
public abstract class WorkflowStartOptions {
    public abstract Integer getWorkflowTimeoutSeconds();

    public static WorkflowStartOptions minimum(final int workflowTimeoutSeconds) {
        return ImmutableWorkflowStartOptions.builder().workflowTimeoutSeconds(workflowTimeoutSeconds).build();
    }
}
