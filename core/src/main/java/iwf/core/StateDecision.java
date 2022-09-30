package iwf.core;

import org.immutables.value.Value;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static iwf.core.StateMovement.COMPLETING_WORKFLOW_MOVEMENT;
import static iwf.core.StateMovement.FAILING_WORKFLOW_MOVEMENT;

@Value.Immutable
public interface StateDecision {

    Optional<List<StateMovement>> getNextStates();

    Optional<Boolean> getWaitForMoreCommandResults();

    StateDecision NO_NEXT = ImmutableStateDecision.builder().build();
    StateDecision COMPLETING_WORKFLOW = ImmutableStateDecision.builder().nextStates(Arrays.asList(COMPLETING_WORKFLOW_MOVEMENT)).build();
    StateDecision FAILING_WORKFLOW = ImmutableStateDecision.builder().nextStates(Arrays.asList(FAILING_WORKFLOW_MOVEMENT)).build();

    StateDecision WAIT_FOR_MORE_RESULTS = ImmutableStateDecision.builder().waitForMoreCommandResults(true).build();
}
