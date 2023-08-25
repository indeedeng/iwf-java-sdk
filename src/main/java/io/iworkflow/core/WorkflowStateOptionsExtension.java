package io.iworkflow.core;

import io.iworkflow.gen.models.ExecuteApiFailurePolicy;
import io.iworkflow.gen.models.WorkflowStateOptions;

// WorkflowStateOptionsExtension provides extension to WorkflowStateOptions
// to make it easier to build
public class WorkflowStateOptionsExtension extends WorkflowStateOptions {

    public WorkflowStateOptions setProceedOnExecuteFailure(WorkflowState proceedingState) {
        this.executeApiFailurePolicy(ExecuteApiFailurePolicy.PROCEED_TO_CONFIGURED_STATE);
        this.executeApiFailureProceedStateId(proceedingState.getStateId());
        return this;
    }

    public WorkflowStateOptions setProceedOnExecuteFailure(WorkflowState proceedingState, WorkflowStateOptions stateOptions) {
        this.executeApiFailurePolicy(ExecuteApiFailurePolicy.PROCEED_TO_CONFIGURED_STATE);
        this.executeApiFailureProceedStateId(proceedingState.getStateId());
        this.executeApiFailureProceedStateOptions(stateOptions);
        return this;
    }
}
