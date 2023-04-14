package io.iworkflow.integ.rpc;

import io.iworkflow.core.Context;
import io.iworkflow.core.StateDecision;
import io.iworkflow.core.WorkflowState;
import io.iworkflow.core.command.CommandRequest;
import io.iworkflow.core.command.CommandResults;
import io.iworkflow.core.communication.Communication;
import io.iworkflow.core.persistence.Persistence;

public class RpcWorkflowState2 implements WorkflowState<Integer> {

    private static int counter = 0;

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
        counter++;
        if (counter == 2) {
            return StateDecision.gracefulCompleteWorkflow(counter);
        } else {
            return StateDecision.gracefulCompleteWorkflow();
        }
    }

    // reset counter so that new test can use it
    public static void resetCounter() {
        counter = 0;
    }
}
