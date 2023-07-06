package io.iworkflow.core;

import io.iworkflow.gen.models.WorkflowConditionalCloseType;
import org.immutables.value.Value;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Value.Immutable
public abstract class StateDecision {

    public abstract Optional<InternalConditionalClose> getWorkflowConditionalClose();

    public abstract List<StateMovement> getNextStates();

    // a dead end will just complete its thread, without triggering any closing workflow
    public static StateDecision deadEnd() {
        return ImmutableStateDecision.builder()
                .nextStates(Arrays.asList(StateMovement.DEAD_END_WORKFLOW_MOVEMENT))
                .build();
    }

    public static ImmutableStateDecision.Builder builder() {
        return ImmutableStateDecision.builder();
    }

    public static StateDecision gracefulCompleteWorkflow(final Object completionOutput) {
        return ImmutableStateDecision.builder().nextStates(Arrays.asList(
                StateMovement.gracefulCompleteWorkflow(completionOutput)
        )).build();
    }

    public static StateDecision gracefulCompleteWorkflow() {
        return ImmutableStateDecision.builder().nextStates(Arrays.asList(
                StateMovement.gracefulCompleteWorkflow()
        )).build();
    }

    public static StateDecision forceCompleteWorkflow(final Object completionOutput) {
        return ImmutableStateDecision.builder().nextStates(Arrays.asList(
                StateMovement.forceCompleteWorkflow(completionOutput)
        )).build();
    }

    public static StateDecision forceCompleteWorkflow() {
        return ImmutableStateDecision.builder().nextStates(Arrays.asList(
                StateMovement.forceCompleteWorkflow()
        )).build();
    }

    public static StateDecision forceFailWorkflow(final Object completionOutput) {
        return ImmutableStateDecision.builder().nextStates(Arrays.asList(
                StateMovement.forceFailWorkflow(completionOutput)
        )).build();
    }

    public static StateDecision forceFailWorkflow() {
        return ImmutableStateDecision.builder()
                .nextStates(Arrays.asList(StateMovement.FORCE_FAILING_WORKFLOW_MOVEMENT))
                .build();
    }


    public static StateDecision forceCompleteIfInternalChannelEmptyOrElse(final String internalChannelName, final Class<? extends WorkflowState> orElseStateClass) {
        return forceCompleteIfInternalChannelEmptyOrElse(internalChannelName, orElseStateClass, null);
    }

    public static StateDecision forceCompleteIfInternalChannelEmptyOrElse(final String internalChannelName, final Class<? extends WorkflowState> orElseStateClass, final Object stateInput) {
        return forceCompleteWithOutputIfInternalChannelEmptyOrElse(null, internalChannelName, StateMovement.create(orElseStateClass, stateInput));
    }

    public static StateDecision forceCompleteIfInternalChannelEmptyOrElse(final String internalChannelName, final StateMovement... orElseStateMovements) {
        return forceCompleteWithOutputIfInternalChannelEmptyOrElse(null, internalChannelName, orElseStateMovements);
    }

    public static StateDecision forceCompleteWithOutputIfInternalChannelEmptyOrElse(final Object completionOutput, final String internalChannelName, final Class<? extends WorkflowState> orElseStateClass) {
        return forceCompleteWithOutputIfInternalChannelEmptyOrElse(completionOutput, internalChannelName, orElseStateClass, null);
    }

    public static StateDecision forceCompleteWithOutputIfInternalChannelEmptyOrElse(final Object completionOutput, final String internalChannelName, final Class<? extends WorkflowState> orElseStateClass, final Object stateInput) {
        return forceCompleteWithOutputIfInternalChannelEmptyOrElse(completionOutput, internalChannelName, StateMovement.create(orElseStateClass, stateInput));
    }

    /**
     * atomically force complete the workflow if internal channel is empty, otherwise trigger the state movements in the thread
     *
     * @param completionOutput     the output of workflow completion
     * @param internalChannelName  the channel name for checking emptiness
     * @param orElseStateMovements the state movements if channel is not empty
     * @return the decision
     */
    public static StateDecision forceCompleteWithOutputIfInternalChannelEmptyOrElse(final Object completionOutput, final String internalChannelName, final StateMovement... orElseStateMovements) {
        return ImmutableStateDecision.builder()
                .workflowConditionalClose(
                        ImmutableInternalConditionalClose.builder()
                                .workflowConditionalCloseType(WorkflowConditionalCloseType.FORCE_COMPLETE_ON_INTERNAL_CHANNEL_EMPTY)
                                .channelName(internalChannelName)
                                .closeInput(Optional.ofNullable(completionOutput))
                                .build()
                )
                .nextStates(Arrays.asList(orElseStateMovements))
                .build();
    }

    public static StateDecision forceCompleteIfSignalChannelEmptyOrElse(final String signalChannelName, final Class<? extends WorkflowState> orElseStateClass) {
        return forceCompleteIfSignalChannelEmptyOrElse(signalChannelName, orElseStateClass, null);
    }

    public static StateDecision forceCompleteIfSignalChannelEmptyOrElse(final String signalChannelName, final Class<? extends WorkflowState> orElseStateClass, final Object stateInput) {
        return forceCompleteWithOutputIfSignalChannelEmptyOrElse(null, signalChannelName, StateMovement.create(orElseStateClass, stateInput));
    }

    public static StateDecision forceCompleteIfSignalChannelEmptyOrElse(final String signalChannelName, final StateMovement... orElseStateMovements) {
        return forceCompleteWithOutputIfSignalChannelEmptyOrElse(null, signalChannelName, orElseStateMovements);
    }

    public static StateDecision forceCompleteWithOutputIfSignalChannelEmptyOrElse(final Object completionOutput, final String signalChannelName, final Class<? extends WorkflowState> orElseStateClass) {
        return forceCompleteWithOutputIfSignalChannelEmptyOrElse(completionOutput, signalChannelName, orElseStateClass, null);
    }

    public static StateDecision forceCompleteWithOutputIfSignalChannelEmptyOrElse(final Object completionOutput, final String signalChannelName, final Class<? extends WorkflowState> orElseStateClass, final Object stateInput) {
        return forceCompleteWithOutputIfSignalChannelEmptyOrElse(completionOutput, signalChannelName, StateMovement.create(orElseStateClass, stateInput));
    }

    /**
     * atomically force complete the workflow if signal channel is empty, otherwise trigger the state movements in the thread
     *
     * @param completionOutput     the output of workflow completion
     * @param signalChannelName    the channel name for checking emptiness
     * @param orElseStateMovements the state movements if channel is not empty
     * @return the decision
     */
    public static StateDecision forceCompleteWithOutputIfSignalChannelEmptyOrElse(final Object completionOutput, final String signalChannelName, final StateMovement... orElseStateMovements) {
        return ImmutableStateDecision.builder()
                .workflowConditionalClose(
                        ImmutableInternalConditionalClose.builder()
                                .workflowConditionalCloseType(WorkflowConditionalCloseType.FORCE_COMPLETE_ON_SIGNAL_CHANNEL_EMPTY)
                                .channelName(signalChannelName)
                                .closeInput(Optional.ofNullable(completionOutput))
                                .build()
                )
                .nextStates(Arrays.asList(orElseStateMovements))
                .build();
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

    public static StateDecision multiNextStates(final List<StateMovement> stateMovements) {
        return ImmutableStateDecision.builder().nextStates(stateMovements).build();
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
