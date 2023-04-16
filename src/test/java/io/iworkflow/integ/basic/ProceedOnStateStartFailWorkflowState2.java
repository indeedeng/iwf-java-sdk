package io.iworkflow.integ.basic;

import io.iworkflow.core.Context;
import io.iworkflow.core.StateDecision;
import io.iworkflow.core.WorkflowState;
import io.iworkflow.core.command.CommandRequest;
import io.iworkflow.core.command.CommandResults;
import io.iworkflow.core.communication.Communication;
import io.iworkflow.core.persistence.Persistence;

public class ProceedOnStateStartFailWorkflowState2 implements WorkflowState<String> {
    private String output = "";

    @Override
    public Class<String> getInputType() {
        return String.class;
    }

    @Override
    public CommandRequest waitUntil(Context context, String input, Persistence persistence, Communication communication) {
        output = input + "_state2_start";
        return CommandRequest.empty;
    }

    @Override
    public StateDecision execute(Context context, String input, CommandResults commandResults, Persistence persistence, Communication communication) {
        output = output + "_state2_decide";
        return StateDecision.gracefulCompleteObjectExecution(output);
    }
}
