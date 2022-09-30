package iwf.core;

import org.immutables.value.Value;

import java.util.Optional;

@Value.Immutable
public interface StateMovement {

    String getStateId();

    Optional<Object> getNextStateInput();

    StateMovement COMPLETING_WORKFLOW_MOVEMENT = ImmutableStateMovement.builder().stateId("_SYS_COMPLETING_WORKFLOW").build();
    StateMovement FAILING_WORKFLOW_MOVEMENT = ImmutableStateMovement.builder().stateId("_SYS_FAILING_WORKFLOW").build();
}
