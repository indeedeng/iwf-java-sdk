package io.iworkflow.core;

import io.iworkflow.gen.models.ExecuteApiFailurePolicy;
import io.iworkflow.gen.models.WorkflowStateOptions;

// WorkflowStateOptionsExtension provides extension to WorkflowStateOptions
// to make it easier to build
public class WorkflowStateOptionsExtension extends WorkflowStateOptions {

    public WorkflowStateOptionsExtension setProceedOnExecuteFailure(final Class<? extends WorkflowState> proceedingState) {
        this.executeApiFailurePolicy(ExecuteApiFailurePolicy.PROCEED_TO_CONFIGURED_STATE);
        this.executeApiFailureProceedStateId(proceedingState.getSimpleName());
        return this;
    }

    public WorkflowStateOptionsExtension setProceedOnExecuteFailure(final Class<? extends WorkflowState> proceedingState, WorkflowStateOptions stateOptions) {
        this.executeApiFailurePolicy(ExecuteApiFailurePolicy.PROCEED_TO_CONFIGURED_STATE);
        this.executeApiFailureProceedStateId(proceedingState.getSimpleName());
        this.executeApiFailureProceedStateOptions(stateOptions);
        return this;
    }
}
