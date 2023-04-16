package io.iworkflow.core;

import io.iworkflow.gen.models.WorkflowStopType;
import org.immutables.value.Value;

import java.util.Optional;

@Value.Immutable
public abstract class StopObjectExecutionOptions {
    public abstract Optional<WorkflowStopType> getWorkflowStopType();

    public abstract Optional<String> getReason();

}
