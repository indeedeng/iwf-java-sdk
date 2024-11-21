package io.iworkflow.integ.rpc;

import io.iworkflow.core.Context;
import io.iworkflow.core.StateDecision;
import io.iworkflow.core.WorkflowState;
import io.iworkflow.core.command.CommandRequest;
import io.iworkflow.core.command.CommandResults;
import io.iworkflow.core.communication.Communication;
import io.iworkflow.core.communication.InternalChannelCommand;
import io.iworkflow.core.persistence.Persistence;

import static io.iworkflow.integ.rpc.RpcLockingWorkflow.RPC_INTERNAL_CHANNEL_NAME;

public class RpcLockingWorkflowState1 implements WorkflowState<Void> {
    @Override
    public Class<Void> getInputType() {
        return Void.class;
    }

    @Override
    public CommandRequest waitUntil(
            Context context,
            Void input,
            Persistence persistence,
            final Communication communication) {
        return CommandRequest.forAnyCommandCompleted(
                InternalChannelCommand.create(RPC_INTERNAL_CHANNEL_NAME)
        );
    }

    @Override
    public StateDecision execute(
            Context context,
            Void input,
            CommandResults commandResults,
            Persistence persistence,
            final Communication communication) {
        return StateDecision.singleNextState(RpcLockingWorkflowState2.class);
    }
}
