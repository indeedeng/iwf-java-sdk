package iwf.core;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import org.immutables.value.Value;

@Value.Immutable
@JsonDeserialize(as = ImmutableWorkflowStartOptions.class)
public abstract class WorkflowStartOptions {
    public abstract Integer getWorkflowTimeoutSeconds();

    public static WorkflowStartOptions minimum(final int workflowTimeoutSeconds) {
        return ImmutableWorkflowStartOptions.builder().workflowTimeoutSeconds(workflowTimeoutSeconds).build();
    }
}
