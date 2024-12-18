package io.iworkflow.integ.stateapifail;

import io.iworkflow.core.WorkflowStateOptions;
import io.iworkflow.gen.models.RetryPolicy;

public class StateFailProceedToRecoverBasic extends StateFailBasic {
    @Override
    public WorkflowStateOptions getStateOptions() {
        return new WorkflowStateOptions()
                .proceedToStateWhenExecuteRetryExhausted(StateRecoverBasic.class)
                .executeApiRetryPolicy(
                        new RetryPolicy()
                                .maximumAttempts(1)
                                .backoffCoefficient(2f)
                );
    }
}
