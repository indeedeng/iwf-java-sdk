package io.github.cadenceoss.iwf.core;

import io.github.cadenceoss.iwf.gen.models.KeyValue;
import org.immutables.value.Value;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Value.Immutable
public abstract class StateDecision {

    public abstract Optional<List<StateMovement>> getNextStates();

    public abstract Optional<Boolean> getWaitForMoreCommandResults();

    public abstract Optional<List<KeyValue>> getUpsertQueryAttributes();

    public static final StateDecision DEAD_END = ImmutableStateDecision.builder().build();

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
