package iwf.core;

/**
 * A holder class for {@link WorkflowState} and it's metadata
 */
public final class StateDef {
    private final WorkflowState workflowState;
    private final boolean canStartWorkflow;

    /**
     * @param workflowState the state
     * @param canStartWorkflow     this indicates whether this state can be used to start the workflow
     */
    public StateDef(final WorkflowState workflowState, final boolean canStartWorkflow) {
        this.workflowState = workflowState;
        this.canStartWorkflow = canStartWorkflow;
    }

    public WorkflowState getWorkflowState() {
        return workflowState;
    }

    public boolean canStartWorkflow() {
        return canStartWorkflow;
    }
}
