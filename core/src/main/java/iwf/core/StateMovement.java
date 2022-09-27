package iwf.core;

import org.immutables.value.Value;

@Value.Immutable
public interface StateMovement {

    String getStateId();

    Object getNextStateInput();

    StateMovement COMPLETING_WORKFLOW_MOVEMENT = ImmutableStateMovement.builder().stateId("_SYS_COMPLETING_WORKFLOW").build();
    StateMovement FAILING_WORKFLOW_MOVEMENT = ImmutableStateMovement.builder().stateId("_SYS_FAILING_WORKFLOW").build();
}
