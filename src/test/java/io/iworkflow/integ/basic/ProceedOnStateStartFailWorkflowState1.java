package io.iworkflow.integ.basic;

import io.iworkflow.core.Context;
import io.iworkflow.core.StateDecision;
import io.iworkflow.core.WorkflowState;
import io.iworkflow.core.WorkflowStateOptions;
import io.iworkflow.core.command.CommandRequest;
import io.iworkflow.core.command.CommandResults;
import io.iworkflow.core.communication.Communication;
import io.iworkflow.core.persistence.Persistence;
import io.iworkflow.gen.models.RetryPolicy;

public class ProceedOnStateStartFailWorkflowState1 implements WorkflowState<String> {
    private String output = "";

    @Override
    public Class<String> getInputType() {
        return String.class;
    }

    @Override
    public CommandRequest waitUntil(Context context, String input, Persistence persistence, Communication communication) {
        output = input + "_state1_start";
        throw new RuntimeException("Start failed");
    }

    @Override
    public StateDecision execute(Context context, String input, CommandResults commandResults, Persistence persistence, Communication communication) {
        if (!context.getAttempt().isPresent()) {
            throw new RuntimeException("attempt must be greater than zero");
        }
        if (!context.getFirstAttemptTimestampSeconds().isPresent()) {
            throw new RuntimeException("firstAttemptTimestampSeconds must be greater than zero");
        }

        output = output + "_state1_decide";
        return StateDecision.singleNextState(ProceedOnStateStartFailWorkflowState2.class, output);
    }

    @Override
    public WorkflowStateOptions getStateOptions() {
        return new WorkflowStateOptions()
                .setProceedToExecuteWhenWaitUntilRetryExhausted(true)
                .setWaitUntilApiRetryPolicy(new RetryPolicy().maximumAttempts(2));
    }
}
