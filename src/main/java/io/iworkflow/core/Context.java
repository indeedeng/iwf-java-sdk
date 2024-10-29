package io.iworkflow.core;

import org.immutables.value.Value;

import java.util.Optional;

@Value.Immutable
public abstract class Context {
    public abstract Long getWorkflowStartTimestampSeconds();

    public abstract Optional<String> getStateExecutionId();

    public abstract String getWorkflowRunId();

    public abstract String getWorkflowId();

    public abstract String getWorkflowType();

    // this is the start time of the first attempt of the API call. It's from ScheduledTimestamp of Cadence/Temporal activity.GetInfo
    // require server version 1.2.2+, return -1 if server version is lower
    public abstract Optional<Long> getFirstAttemptTimestampSeconds();

    // Attempt starts from 1, and increased by 1 for every retry if retry policy is specified. It's from Attempt of Cadence/Temporal activity.GetInfo
    // require server version 1.2.2+, return -1 if server version is lower
    public abstract Optional<Integer> getAttempt();
}
