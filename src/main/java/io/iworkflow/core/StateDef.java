package io.iworkflow.core;

import org.immutables.value.Value;

/**
 * A holder class for {@link WorkflowState} and it's metadata
 */
@Value.Immutable
public abstract class StateDef {

    public abstract WorkflowState getWorkflowState();

    // indicates if this state can be used to start a workflow
    public abstract boolean getCanStartWorkflow();

    public static StateDef startingState(WorkflowState state) {
        return ImmutableStateDef.builder()
                .canStartWorkflow(true)
                .workflowState(
                        state
                )
                .build();
    }

    public static StateDef nonStartingState(WorkflowState state) {
        return ImmutableStateDef.builder()
                .canStartWorkflow(false)
                .workflowState(
                        state
                )
                .build();
    }
}
