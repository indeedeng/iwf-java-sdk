package io.iworkflow.integ.stateapifail;

import io.iworkflow.core.WorkflowStateOptions;
import io.iworkflow.core.WorkflowStateOptionsExtension;
import io.iworkflow.gen.models.RetryPolicy;

public class StateFailProceedToRecoverBasic extends StateFailBasic {
    @Override
    public WorkflowStateOptions getStateOptions() {
        return new WorkflowStateOptionsExtension()
                .setProceedWhenExecuteRetryExhausted(StateRecoverBasic.class)
                .executeApiRetryPolicy(
                        new RetryPolicy()
                                .maximumAttempts(1)
                                .backoffCoefficient(2f)
                );
    }
}
