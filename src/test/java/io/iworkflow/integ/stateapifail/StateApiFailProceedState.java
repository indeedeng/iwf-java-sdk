package io.iworkflow.integ.stateapifail;

import io.iworkflow.core.WorkflowStateOptionsExtension;
import io.iworkflow.gen.models.RetryPolicy;
import io.iworkflow.gen.models.WorkflowStateOptions;

public class StateApiFailProceedState extends StateApiFailWorkflowState1 {
    @Override
    public WorkflowStateOptions getStateOptions() {
        return new WorkflowStateOptionsExtension()
                .setProceedOnExecuteFailure(StateApiRecoverState.class)
                .executeApiRetryPolicy(
                        new RetryPolicy()
                                .maximumAttempts(1)
                                .backoffCoefficient(2f)
                );
    }
}
