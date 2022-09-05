package iwf.core;

public class StateMovement {

    static StateMovement COMPLETING_WORKFLOW_MOVEMENT = new StateMovement("_SYS_COMPLETING_WORKFLOW", null);
    static StateMovement FAILING_WORKFLOW_MOVEMENT = new StateMovement("_SYS_FAILING_WORKFLOW", null);

    private final String stateId;
    private final Object nextStateInput;

    /**
     * @param stateId        the stateId of the next state
     * @param nextStateInput the input of next state.
     *                       This must match the input type of next state otherwise a runtime exception will be thrown
     */
    public StateMovement(final String stateId, final Object nextStateInput) {
        this.stateId = stateId;
        this.nextStateInput = nextStateInput;
    }

    public StateMovement(final String stateId) {
        this(stateId, null);
    }

    public String getStateId() {
        return stateId;
    }

    public Object getNextStateInput() {
        return nextStateInput;
    }
}
