package io.iworkflow.integ.rpc;

import io.iworkflow.core.Context;
import io.iworkflow.core.StateDecision;
import io.iworkflow.core.WorkflowState;
import io.iworkflow.core.command.CommandRequest;
import io.iworkflow.core.command.CommandResults;
import io.iworkflow.core.communication.Communication;
import io.iworkflow.core.communication.InternalChannelCommand;
import io.iworkflow.core.persistence.Persistence;

import static io.iworkflow.integ.rpc.RpcWorkflow.INTERNAL_CHANNEL_NAME;

public class RpcLockingWorkflowState2 implements WorkflowState<Void> {

    private static int counter = 0;

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
        return CommandRequest.empty;
    }

    @Override
    public StateDecision execute(
            Context context,
            Void input,
            CommandResults commandResults,
            Persistence persistence,
            final Communication communication) {
        counter++;
        return StateDecision.gracefulCompleteWorkflow("The execute method was executed " + counter + " times");

    }

    // reset counter so that new test can use it
    public static int resetCounter() {
        final int old = counter;
        counter = 0;
        return old;
    }
}
