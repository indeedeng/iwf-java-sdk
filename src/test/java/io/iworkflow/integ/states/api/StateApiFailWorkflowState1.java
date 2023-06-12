package io.iworkflow.integ.states.api;

import io.iworkflow.core.Context;
import io.iworkflow.core.StateDecision;
import io.iworkflow.core.WorkflowState;
import io.iworkflow.core.command.CommandRequest;
import io.iworkflow.core.command.CommandResults;
import io.iworkflow.core.communication.Communication;
import io.iworkflow.core.persistence.Persistence;
import io.iworkflow.gen.models.RetryPolicy;
import io.iworkflow.gen.models.WorkflowStateOptions;

public class StateApiFailWorkflowState1 implements WorkflowState<Integer> {

    @Override
    public Class<Integer> getInputType() {
        return Integer.class;
    }

    @Override
    public CommandRequest waitUntil(
            Context context,
            Integer input,
            Persistence persistence,
            final Communication communication) {
        return CommandRequest.empty;
    }

    @Override
    public StateDecision execute(
            Context context,
            Integer input,
            CommandResults commandResults,
            Persistence persistence,
            final Communication communication) {
        throw new RuntimeException("test api failing");
    }

    @Override
    public WorkflowStateOptions getStateOptions() {
        return new WorkflowStateOptions().executeApiRetryPolicy(
                new RetryPolicy()
                        .maximumAttempts(1)
                        .backoffCoefficient(2f)
        );
    }
}
