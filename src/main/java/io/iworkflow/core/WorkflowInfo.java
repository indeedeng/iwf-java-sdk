package io.iworkflow.core;

import io.iworkflow.gen.models.WorkflowStatus;
import org.immutables.value.Value;

@Value.Immutable
public abstract class WorkflowInfo {
    public abstract WorkflowStatus getWorkflowStatus();

    public static ImmutableWorkflowInfo.Builder builder() {
        return ImmutableWorkflowInfo.builder();
    }
}
