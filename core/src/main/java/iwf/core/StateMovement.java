package iwf.core;

import org.immutables.value.Value;

import java.util.Optional;

@Value.Immutable
public abstract class StateMovement {

    public abstract String getStateId();

    public abstract Optional<Object> getNextStateInput();

    private final static String GRACEFUL_COMPLETING_WORKFLOW_STATE_ID = "_SYS_GRACEFUL_COMPLETING_WORKFLOW";
    private final static String FORCE_FAILING_WORKFLOW_STATE_ID = "_SYS_FORCE_FAILING_WORKFLOW";
    public static final StateMovement GRACEFUL_COMPLETING_WORKFLOW = ImmutableStateMovement.builder().stateId(GRACEFUL_COMPLETING_WORKFLOW_STATE_ID).build();
    public static final StateMovement FORCE_FAILING_WORKFLOW_MOVEMENT = ImmutableStateMovement.builder().stateId(FORCE_FAILING_WORKFLOW_STATE_ID).build();

    public static StateMovement gracefulCompleteWorkflow(Object output) {
        return ImmutableStateMovement.builder().stateId(GRACEFUL_COMPLETING_WORKFLOW_STATE_ID)
                .nextStateInput(output)
                .build();
    }
}
