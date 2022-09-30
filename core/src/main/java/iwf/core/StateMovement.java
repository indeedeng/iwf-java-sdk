package iwf.core;

import org.immutables.value.Value;

import java.util.Optional;

@Value.Immutable
public abstract class StateMovement {

    public abstract String getStateId();

    public abstract Optional<Object> getNextStateInput();

    public static final StateMovement COMPLETING_WORKFLOW_MOVEMENT = ImmutableStateMovement.builder().stateId("_SYS_COMPLETING_WORKFLOW").build();
    public static final StateMovement FAILING_WORKFLOW_MOVEMENT = ImmutableStateMovement.builder().stateId("_SYS_FAILING_WORKFLOW").build();
}
