package io.iworkflow.integ.reset;

import io.iworkflow.core.Context;
import io.iworkflow.core.StateDecision;
import io.iworkflow.core.WorkflowState;
import io.iworkflow.core.command.CommandRequest;
import io.iworkflow.core.command.CommandResults;
import io.iworkflow.core.communication.Communication;
import io.iworkflow.core.communication.SignalCommand;
import io.iworkflow.core.persistence.Persistence;
import io.iworkflow.integ.rpc.RpcLockingWorkflowState2;

import static io.iworkflow.integ.reset.RpcLockingWorkflowResetWithStateExecutionId.SIGNAL_CHANNEL_NAME;

public class RpcLockingWorkflowStateResetWithStateExecutionId1 implements WorkflowState<Void> {
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
        return CommandRequest.forAllCommandCompleted(SignalCommand.create(SIGNAL_CHANNEL_NAME));
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
