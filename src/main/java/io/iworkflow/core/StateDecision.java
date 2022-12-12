package io.iworkflow.core;

import org.immutables.value.Value;

import java.util.Arrays;
import java.util.List;

@Value.Immutable
public abstract class StateDecision {

    public abstract List<StateMovement> getNextStates();

    public static final StateDecision DEAD_END = ImmutableStateDecision.builder().build();

    public static final StateDecision FORCE_FAILING_WORKFLOW = ImmutableStateDecision.builder()
            .nextStates(Arrays.asList(StateMovement.FORCE_FAILING_WORKFLOW_MOVEMENT))
            .build();

    public static ImmutableStateDecision.Builder builder() {
        return ImmutableStateDecision.builder();
    }

    public static StateDecision gracefulCompleteWorkflow(final Object output) {
        return ImmutableStateDecision.builder().nextStates(Arrays.asList(
                StateMovement.gracefulCompleteWorkflow(output)
        )).build();
    }

    public static StateDecision forceCompleteWorkflow(final Object output) {
        return ImmutableStateDecision.builder().nextStates(Arrays.asList(
                StateMovement.forceCompleteWorkflow(output)
        )).build();
    }

    public static StateDecision gracefulCompleteWorkflow() {
        return ImmutableStateDecision.builder().nextStates(Arrays.asList(
        )).build();
    }

    public static StateDecision forceCompleteWorkflow() {
        return ImmutableStateDecision.builder().nextStates(Arrays.asList(
        )).build();
    }

    public static StateDecision singleNextState(final String stateId) {
        return ImmutableStateDecision.builder().nextStates(Arrays.asList(
                ImmutableStateMovement.builder().stateId(stateId).build()
        )).build();
    }

    public static StateDecision singleNextState(final String stateId, final Object stateInput) {
        return ImmutableStateDecision.builder().nextStates(Arrays.asList(
                ImmutableStateMovement.builder().stateId(stateId)
                        .nextStateInput(stateInput)
                        .build()
        )).build();
    }

    public static StateDecision multiNextStates(final StateMovement... stateMovements) {
        return ImmutableStateDecision.builder().nextStates(Arrays.asList(stateMovements)).build();
    }
}
