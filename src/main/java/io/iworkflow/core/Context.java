package io.iworkflow.core;

import org.immutables.value.Value;

import java.util.Optional;

@Value.Immutable
public abstract class Context {
    public abstract Long getWorkflowStartTimestampSeconds();

    /**
     * @return the StateExecutionId.
     * Only applicable for state methods (waitUntil or execute)
     */
    public abstract Optional<String> getStateExecutionId();

    public abstract String getWorkflowRunId();

    public abstract String getWorkflowId();

    public abstract String getWorkflowType();

    /**
     * @return the start time of the first attempt of the state method invocation.
     * Only applicable for state methods (waitUntil or execute)
     */
    public abstract Optional<Long> getFirstAttemptTimestampSeconds();

    /**
     * @return attempt starts from 1, and increased by 1 for every retry if retry policy is specified.
     */
    public abstract Optional<Integer> getAttempt();

    /**
     * @return the requestId that is used to start the child workflow from state method.
     * Only applicable for state methods (waitUntil or execute)
     */
    public abstract Optional<String> getChildWorkflowRequestId();
}
