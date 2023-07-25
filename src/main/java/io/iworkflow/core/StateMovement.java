package io.iworkflow.core;

import io.iworkflow.gen.models.WorkflowStateOptions;
import org.immutables.value.Value;

import java.util.Optional;

@Value.Immutable
public abstract class StateMovement {

    public abstract String getStateId();

    public abstract Optional<Object> getStateInput();
    public abstract Optional<WorkflowStateOptions> getStateOptions();

    public final static String RESERVED_STATE_ID_PREFIX = "_SYS_";
    private final static String GRACEFUL_COMPLETING_WORKFLOW_STATE_ID = "_SYS_GRACEFUL_COMPLETING_WORKFLOW";
    private final static String FORCE_COMPLETING_WORKFLOW_STATE_ID = "_SYS_FORCE_COMPLETING_WORKFLOW";
    private final static String FORCE_FAILING_WORKFLOW_STATE_ID = "_SYS_FORCE_FAILING_WORKFLOW";

    // a dead end will just complete its thread, without triggering any closing workflow
    private final static String DEAD_END_WORKFLOW_STATE_ID = "_SYS_DEAD_END";

    public static StateMovement gracefulCompleteWorkflow() {
        return ImmutableStateMovement.builder().stateId(GRACEFUL_COMPLETING_WORKFLOW_STATE_ID)
                .build();
    }

    public static StateMovement gracefulCompleteWorkflow(final Object output) {
        return ImmutableStateMovement.builder().stateId(GRACEFUL_COMPLETING_WORKFLOW_STATE_ID)
                .stateInput(output)
                .build();
    }

    public static StateMovement forceCompleteWorkflow() {
        return ImmutableStateMovement.builder().stateId(FORCE_COMPLETING_WORKFLOW_STATE_ID)
                .build();
    }

    public static StateMovement forceCompleteWorkflow(final Object output) {
        return ImmutableStateMovement.builder().stateId(FORCE_COMPLETING_WORKFLOW_STATE_ID)
                .stateInput(output)
                .build();
    }

    public static final StateMovement DEAD_END_WORKFLOW_MOVEMENT = ImmutableStateMovement.builder().stateId(DEAD_END_WORKFLOW_STATE_ID).build();
    public static final StateMovement FORCE_FAILING_WORKFLOW_MOVEMENT = ImmutableStateMovement.builder().stateId(FORCE_FAILING_WORKFLOW_STATE_ID).build();

    public static StateMovement forceFailWorkflow(final Object output) {
        return ImmutableStateMovement.builder().stateId(FORCE_FAILING_WORKFLOW_STATE_ID)
                .stateInput(output)
                .build();
    }

    /**
     *
     * @param stateClass    required
     * @param stateInput    required
     * @param stateOptions  required, to override the defined ones in the State class
     * @return state movement
     */
    public static StateMovement create(final Class<? extends WorkflowState> stateClass, final Object stateInput, final WorkflowStateOptions stateOptions) {
        return create(stateClass.getSimpleName(), stateInput, stateOptions);
    }

    /**
     *
     * @param stateClass    required
     * @param stateInput    required
     * @return state movement
     */
    public static StateMovement create(final Class<? extends WorkflowState> stateClass, final Object stateInput) {
        return create(stateClass, stateInput, null);
    }

    /**
     *
     * @param stateClass    required
     * @param stateOptions  required, to override the defined ones in the State class
     * @return state movement
     */
    public static StateMovement create(final Class<? extends WorkflowState> stateClass, final WorkflowStateOptions stateOptions) {
        return create(stateClass, null, stateOptions);
    }

    /**
     *
     * @param stateClass    required
     * @return state movement
     */
    public static StateMovement create(final Class<? extends WorkflowState> stateClass) {
        return create(stateClass, null, null);
    }

    /**
     * use the other one with WorkflowState class param if the stateId is provided by default, to make your code cleaner
     * @param stateId       stateId
     * @param stateInput    input
     * @param stateOptions  required, to override the defined ones in the State class
     * @return state movement
     */
    public static StateMovement create(final String stateId, final Object stateInput, final WorkflowStateOptions stateOptions) {
        if (stateId.startsWith(RESERVED_STATE_ID_PREFIX)) {
            throw new WorkflowDefinitionException("Cannot use reserved stateId prefix for your stateId");
        }

        final ImmutableStateMovement.Builder builder = ImmutableStateMovement.builder()
                .stateId(stateId);

        if (stateInput != null) {
            builder.stateInput(stateInput);
        }

        if (stateOptions != null) {
            builder.stateOptions(stateOptions);
        }

        return builder.build();
    }
}
