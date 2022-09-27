package iwf.core;

import org.immutables.value.Value;

@Value.Immutable
public interface Context {
    Integer getWorkflowStartTimestampSeconds();

    String getStateExecutionId();

    String getWorkflowRunId();

    String getWorkflowId();
}
