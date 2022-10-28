package io.github.cadenceoss.iwf.core;

import org.immutables.value.Value;

import java.util.Optional;

@Value.Immutable
public abstract class StateMovement {

    public abstract String getStateId();

    public abstract Optional<Object> getNextStateInput();

    private final static String GRACEFUL_COMPLETING_WORKFLOW_STATE_ID = "_SYS_GRACEFUL_COMPLETING_WORKFLOW";
    private final static String FORCE_COMPLETING_WORKFLOW_STATE_ID = "_SYS_FORCE_COMPLETING_WORKFLOW";
    private final static String FORCE_FAILING_WORKFLOW_STATE_ID = "_SYS_FORCE_FAILING_WORKFLOW";
    public static final StateMovement FORCE_FAILING_WORKFLOW_MOVEMENT = ImmutableStateMovement.builder().stateId(FORCE_FAILING_WORKFLOW_STATE_ID).build();

    public static StateMovement gracefulCompleteWorkflow(final Object output) {
        return ImmutableStateMovement.builder().stateId(GRACEFUL_COMPLETING_WORKFLOW_STATE_ID)
                .nextStateInput(output)
                .build();
    }

    public static StateMovement forceCompleteWorkflow(final Object output) {
        return ImmutableStateMovement.builder().stateId(FORCE_COMPLETING_WORKFLOW_STATE_ID)
                .nextStateInput(output)
                .build();
    }

    public static StateMovement gracefulCompleteWorkflow() {
        return ImmutableStateMovement.builder().stateId(GRACEFUL_COMPLETING_WORKFLOW_STATE_ID)
                .build();
    }

    public static StateMovement forceCompleteWorkflow() {
        return ImmutableStateMovement.builder().stateId(FORCE_COMPLETING_WORKFLOW_STATE_ID)
                .build();
    }

    public static StateMovement create(final String stateId, final Object stateInput) {
        return ImmutableStateMovement.builder().stateId(stateId)
                .nextStateInput(stateInput)
                .build();
    }

    public static StateMovement create(final String stateId) {
        return ImmutableStateMovement.builder().stateId(stateId)
                .build();
    }
}
