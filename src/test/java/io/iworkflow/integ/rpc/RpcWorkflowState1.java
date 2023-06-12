package io.iworkflow.integ.rpc;

import io.iworkflow.core.Context;
import io.iworkflow.core.StateDecision;
import io.iworkflow.core.WorkflowState;
import io.iworkflow.core.command.CommandRequest;
import io.iworkflow.core.command.CommandResults;
import io.iworkflow.core.communication.Communication;
import io.iworkflow.core.communication.InternalChannelCommand;
import io.iworkflow.core.persistence.Persistence;

import static io.iworkflow.integ.rpc.Keys.INTERNAL_CHANNEL_NAME;

public class RpcWorkflowState1 implements WorkflowState<Integer> {

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
        return CommandRequest.forAnyCommandCompleted(
                InternalChannelCommand.create(INTERNAL_CHANNEL_NAME)
        );
    }

    @Override
    public StateDecision execute(
            Context context,
            Integer input,
            CommandResults commandResults,
            Persistence persistence,
            final Communication communication) {
        return StateDecision.singleNextState(RpcWorkflowState2.class, 0);
    }
}
