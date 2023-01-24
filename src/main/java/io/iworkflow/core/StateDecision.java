package io.iworkflow.core;

import org.immutables.value.Value;

import java.util.ArrayList;
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
                StateMovement.gracefulCompleteWorkflow()
        )).build();
    }

    public static StateDecision forceCompleteWorkflow() {
        return ImmutableStateDecision.builder().nextStates(Arrays.asList(
                StateMovement.forceCompleteWorkflow()
        )).build();
    }

    public static StateDecision singleNextState(final Class<? extends WorkflowState> stateClass) {
        return singleNextState(stateClass.getSimpleName());
    }

    /**
     * use the other one with WorkflowState class param if the StateId is provided by default, to make your code cleaner
     *
     * @param stateId stateId
     * @return state decision
     */
    public static StateDecision singleNextState(final String stateId) {
        return ImmutableStateDecision.builder().nextStates(Arrays.asList(
                StateMovement.create(stateId)
        )).build();
    }

    public static StateDecision singleNextState(final Class<? extends WorkflowState> stateClass, final Object stateInput) {
        return singleNextState(stateClass.getSimpleName(), stateInput);
    }

    /**
     * use the other one with WorkflowState class param if the StateId is provided by default, to make your code cleaner
     * @param stateId stateId of next state
     * @param stateInput input for next state
     * @return state decision
     */
    public static StateDecision singleNextState(final String stateId, final Object stateInput) {
        return ImmutableStateDecision.builder().nextStates(Arrays.asList(
                StateMovement.create(stateId, stateInput)
        )).build();
    }

    public static StateDecision multiNextStates(final StateMovement... stateMovements) {
        return ImmutableStateDecision.builder().nextStates(Arrays.asList(stateMovements)).build();
    }

    public static StateDecision multiNextStates(final Class<? extends WorkflowState>... states) {
        List<String> stateIds = new ArrayList<>();
        Arrays.stream(states).forEach(s -> stateIds.add(s.getSimpleName()));
        return multiNextStates(stateIds.toArray(new String[0]));
    }

    /**
     * use the other one with WorkflowState class param if the StateId is provided by default, to make your code cleaner
     * @param stateIds stateIds of next states
     * @return state decision
     */
    public static StateDecision multiNextStates(final String... stateIds) {
        final ArrayList<StateMovement> stateMovements = new ArrayList<StateMovement>();
        Arrays.stream(stateIds).forEach(id -> {
            stateMovements.add(StateMovement.create(id));
        });
        return ImmutableStateDecision.builder().nextStates(stateMovements).build();
    }
}
