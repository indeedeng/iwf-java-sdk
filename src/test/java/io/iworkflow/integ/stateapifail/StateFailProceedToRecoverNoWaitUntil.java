package io.iworkflow.integ.stateapifail;

import io.iworkflow.core.WorkflowStateOptions;
import io.iworkflow.gen.models.RetryPolicy;

public class StateFailProceedToRecoverNoWaitUntil extends StateFailBasic {
    @Override
    public WorkflowStateOptions getStateOptions() {
        return new WorkflowStateOptions()
                .setProceedToStateWhenExecuteRetryExhausted(StateRecoverNoWaitUntil.class)
                .setExecuteApiRetryPolicy(
                        new RetryPolicy()
                                .maximumAttempts(1)
                                .backoffCoefficient(2f)
                );
    }
}
