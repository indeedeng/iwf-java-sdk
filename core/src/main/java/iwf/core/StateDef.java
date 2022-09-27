package iwf.core;

import org.immutables.value.Value;

/**
 * A holder class for {@link WorkflowState} and it's metadata
 */
@Value.Immutable
public interface StateDef {

    WorkflowState getWorkflowState();

    // indicates if this state can be used to start a workflow
    boolean canStartWorkflow();
}
