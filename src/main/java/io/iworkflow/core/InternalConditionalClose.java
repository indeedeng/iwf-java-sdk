package io.iworkflow.core;

import io.iworkflow.gen.models.WorkflowConditionalCloseType;
import org.immutables.value.Value;

import java.util.Optional;

@Value.Immutable
public abstract class InternalConditionalClose {
    public abstract WorkflowConditionalCloseType getWorkflowConditionalCloseType();

    public abstract String getChannelName();

    public abstract Optional<Object> getCloseInput();
}
