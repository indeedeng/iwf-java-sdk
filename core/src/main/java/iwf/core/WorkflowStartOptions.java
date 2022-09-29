package iwf.core;

import org.immutables.value.Value;

@Value.Immutable
public interface WorkflowStartOptions {
    Long getWorkflowTimeoutSeconds();
}
