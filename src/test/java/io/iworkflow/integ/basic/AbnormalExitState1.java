package io.iworkflow.integ.basic;

import io.iworkflow.core.Context;
import io.iworkflow.core.StateDecision;
import io.iworkflow.core.WorkflowState;
import io.iworkflow.core.WorkflowStateOptions;
import io.iworkflow.core.command.CommandResults;
import io.iworkflow.core.communication.Communication;
import io.iworkflow.core.persistence.Persistence;
import io.iworkflow.gen.models.RetryPolicy;

public class AbnormalExitState1 implements WorkflowState<Integer> {

    @Override
    public Class<Integer> getInputType() {
        return Integer.class;
    }

    @Override
    public StateDecision execute(
            final Context context,
            final Integer input,
            final CommandResults commandResults,
            Persistence persistence,
            final Communication communication) {
        throw new RuntimeException("abnormal exit state");
    }

    @Override
    public WorkflowStateOptions getStateOptions() {
        return new WorkflowStateOptions().setExecuteApiRetryPolicy(
                new RetryPolicy()
                        .maximumAttempts(1)
                        .backoffCoefficient(2f)
        );
    }
}