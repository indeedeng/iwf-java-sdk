package iwf.core;

import org.immutables.value.Value;

import java.util.List;
import java.util.Map;

import static iwf.core.StateMovement.COMPLETING_WORKFLOW_MOVEMENT;
import static iwf.core.StateMovement.FAILING_WORKFLOW_MOVEMENT;

@Value.Immutable
public interface StateDecision {

    List<StateMovement> getNextStates();

    Map<String, Object> getUpsertSearchAttributes();

    Map<String, Object> getUpsertQueryAttributes();

    boolean getWaitForMoreCommandResults();

    StateDecision COMPLETING_WORKFLOW = ImmutableStateDecision.builder().addNextStates(COMPLETING_WORKFLOW_MOVEMENT).build();
    StateDecision FAILING_WORKFLOW = ImmutableStateDecision.builder().addNextStates(FAILING_WORKFLOW_MOVEMENT).build();

    StateDecision DECISION_WAIT_FOR_MORE_RESULTS = ImmutableStateDecision.builder().waitForMoreCommandResults(true).build();
}
