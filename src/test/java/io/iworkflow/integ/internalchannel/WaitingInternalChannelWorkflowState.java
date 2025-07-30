package io.iworkflow.integ.internalchannel;

import io.iworkflow.core.Context;
import io.iworkflow.core.StateDecision;
import io.iworkflow.core.WorkflowState;
import io.iworkflow.core.command.CommandRequest;
import io.iworkflow.core.command.CommandResults;
import io.iworkflow.core.communication.Communication;
import io.iworkflow.core.communication.InternalChannelCommand;
import io.iworkflow.core.communication.InternalChannelCommandResult;
import io.iworkflow.core.persistence.Persistence;

public class WaitingInternalChannelWorkflowState implements WorkflowState<Integer> {

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
        return CommandRequest.forAllCommandCompleted(
                InternalChannelCommand.create(WaitingInternalChannelWorkflow.INTER_STATE_CHANNEL_NAME),
                InternalChannelCommand.create(WaitingInternalChannelWorkflow.INTER_STATE_CHANNEL_NAME)
        );
    }

    @Override
    public StateDecision execute(
            Context context,
            Integer input,
            CommandResults commandResults,
            Persistence persistence,
            final Communication communication) {
        final InternalChannelCommandResult result1 = commandResults.getAllInternalChannelCommandResult().get(0);
        final InternalChannelCommandResult result2 = commandResults.getAllInternalChannelCommandResult().get(1);

        Integer output = input + (Integer) result1.getValue().get() + (Integer) result2.getValue().get();

        return StateDecision.gracefulCompleteWorkflow(output);
    }
}
