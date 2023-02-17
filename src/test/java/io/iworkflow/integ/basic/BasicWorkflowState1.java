package io.iworkflow.integ.basic;

import io.iworkflow.core.Context;
import io.iworkflow.core.StateDecision;
import io.iworkflow.core.WorkflowState;
import io.iworkflow.core.command.CommandRequest;
import io.iworkflow.core.command.CommandResults;
import io.iworkflow.core.communication.Communication;
import io.iworkflow.core.persistence.Persistence;

public class BasicWorkflowState1 implements WorkflowState<Integer> {

    @Override
    public Class<Integer> getInputType() {
        return Integer.class;
    }

    @Override
    public CommandRequest start(final Context context, final Integer input, Persistence persistence, final Communication communication) {
        if (context.getAttempt() <= 0) {
            throw new RuntimeException("attempt must be greater than zero");
        }
        if (context.getFirstAttemptTimestampSeconds() <= 0) {
            throw new RuntimeException("firstAttemptTimestampSeconds must be greater than zero");
        }
        return CommandRequest.empty;
    }

    @Override
    public StateDecision decide(final Context context, final Integer input, final CommandResults commandResults, Persistence persistence, final Communication communication) {
        if (context.getAttempt() <= 0) {
            throw new RuntimeException("attempt must be greater than zero");
        }
        if (context.getFirstAttemptTimestampSeconds() <= 0) {
            throw new RuntimeException("firstAttemptTimestampSeconds must be greater than zero");
        }

        final int output = input + 1;
        if (commandResults.getStateStartApiSucceeded())
            return StateDecision.singleNextState(BasicWorkflowState2.class, output);
        else
            return StateDecision.singleNextState(BasicWorkflowState3.class, output);
    }
}