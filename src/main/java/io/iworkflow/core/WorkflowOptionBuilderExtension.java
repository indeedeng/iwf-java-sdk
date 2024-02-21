package io.iworkflow.core;

import java.util.Arrays;

/**
 * This class is for extending {@link ImmutableWorkflowOptions.Builder} to provide a
 * better experience with strongly typing.
 */
public class WorkflowOptionBuilderExtension {
    private ImmutableWorkflowOptions.Builder builder = ImmutableWorkflowOptions.builder();

    /**
     * Add a state to wait for completion. This only waiting for the first completion of the state
     * @param states The states to wait for completion. O
     * @return The builder.
     */
    public WorkflowOptionBuilderExtension WaitForCompletionStates(Class<? extends WorkflowState> ...states) {
        Arrays.stream(states).forEach(
                state -> builder.addWaitForCompletionStateExecutionIds(
                        WorkflowState.getStateExecutionId(state,1)
                ));
        return this;
    }

    /**
     * Add a state to wait for completion. This can wait for any times completion of the state
     * @param state The state to wait for completion.
     * @param number The number of the state completion to wait for. E.g. when it's 2, it's waiting for the second completion of the state.
     * @return The builder.
     */
    public WorkflowOptionBuilderExtension WaitForCompletionStateWithNumber(Class<? extends WorkflowState> state, int number) {
        builder.addWaitForCompletionStateExecutionIds(
                WorkflowState.getStateExecutionId(state, number)
        );
        return this;
    }

    public ImmutableWorkflowOptions.Builder  getBuilder() {
        return builder;
    }
}
