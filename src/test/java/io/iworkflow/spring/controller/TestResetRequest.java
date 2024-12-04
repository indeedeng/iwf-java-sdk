package io.iworkflow.spring.controller;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import io.iworkflow.gen.models.WorkflowResetType;
import org.immutables.value.Value;

import java.util.Optional;

@Value.Immutable
@JsonDeserialize(as = ImmutableTestResetRequest.class)
public abstract class TestResetRequest {
    public abstract WorkflowResetType getResetType();

    public abstract Optional<Integer> getHistoryEventId();

    public abstract String getReason();

    public abstract Optional<String> getHistoryEventTime();

    public abstract Optional<String> getStateId();

    public abstract Optional<String> getStateExecutionId();

    public abstract Optional<Boolean> getSkipSignalReapply();

    public abstract Optional<Boolean> getSkipUpdateReapply();

    public abstract String getRunId();
}
