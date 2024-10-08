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
    public CommandRequest waitUntil(final Context context, final Integer input, Persistence persistence, final Communication communication) {
        if (!context.getAttempt().isPresent()) {
            throw new RuntimeException("attempt must be greater than zero");
        }
        if (!context.getFirstAttemptTimestampSeconds().isPresent()) {
            throw new RuntimeException("firstAttemptTimestampSeconds must be greater than zero");
        }
        return CommandRequest.empty;
    }

    @Override
    public StateDecision execute(final Context context, final Integer input, final CommandResults commandResults, Persistence persistence, final Communication communication) {
        if (!context.getAttempt().isPresent()) {
            throw new RuntimeException("attempt must be greater than zero");
        }
        if (!context.getFirstAttemptTimestampSeconds().isPresent()) {
            throw new RuntimeException("firstAttemptTimestampSeconds must be greater than zero");
        }

        final int output = input + 1;
        return StateDecision.singleNextState(BasicWorkflowState2.class, output, "testKey");
    }
}