package iwf.core;

import org.immutables.value.Value;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static iwf.core.StateMovement.FORCE_FAILING_WORKFLOW_MOVEMENT;
import static iwf.core.StateMovement.GRACEFUL_COMPLETING_WORKFLOW;

@Value.Immutable
public abstract class StateDecision {

    public abstract Optional<List<StateMovement>> getNextStates();

    public abstract Optional<Boolean> getWaitForMoreCommandResults();

    public static final StateDecision DEAD_END = ImmutableStateDecision.builder().build();
    public static final StateDecision COMPLETING_WORKFLOW = ImmutableStateDecision.builder().nextStates(Arrays.asList(GRACEFUL_COMPLETING_WORKFLOW)).build();
    public static final StateDecision FAILING_WORKFLOW = ImmutableStateDecision.builder().nextStates(Arrays.asList(FORCE_FAILING_WORKFLOW_MOVEMENT)).build();

    public static final StateDecision WAIT_FOR_MORE_RESULTS = ImmutableStateDecision.builder().waitForMoreCommandResults(true).build();

    public static StateDecision gracefulCompleteWorkflow(final Object output) {
        return ImmutableStateDecision.builder().nextStates(Arrays.asList(
                StateMovement.gracefulCompleteWorkflow(output)
        )).build();
    }

    public static StateDecision singleNextState(final String stateId, final Object stateInput) {
        return ImmutableStateDecision.builder().nextStates(Arrays.asList(
                ImmutableStateMovement.builder().stateId(stateId)
                        .nextStateInput(stateInput)
                        .build()
        )).build();
    }
}
