package io.iworkflow.integ.reset;

import io.iworkflow.core.Context;
import io.iworkflow.core.StateDecision;
import io.iworkflow.core.WorkflowState;
import io.iworkflow.core.command.CommandRequest;
import io.iworkflow.core.command.CommandResults;
import io.iworkflow.core.communication.Communication;
import io.iworkflow.core.persistence.Persistence;

public class RpcLockingWorkflowStateResetWithStateExecutionId2 implements WorkflowState<Void> {

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
        if (counter > 4) {
            return StateDecision.gracefulCompleteWorkflow("The execute method was executed " + counter + " times");
        }
        return StateDecision.singleNextState(RpcLockingWorkflowStateResetWithStateExecutionId2.class);

    }

    // reset counter so that new test can use it
    public static int resetCounter() {
        final int old = counter;
        counter = 0;
        return old;
    }
}
