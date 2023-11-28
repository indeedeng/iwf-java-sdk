package io.iworkflow.integ.stateapifail;

import io.iworkflow.core.Context;
import io.iworkflow.core.StateDecision;
import io.iworkflow.core.WorkflowState;
import io.iworkflow.core.command.CommandRequest;
import io.iworkflow.core.command.CommandResults;
import io.iworkflow.core.communication.Communication;
import io.iworkflow.core.persistence.Persistence;

public class StateRecoverBasic implements WorkflowState<Integer> {

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
        if(input == 10){
            return StateDecision.gracefulCompleteWorkflow(input);
        }else if(input == 5){
            return StateDecision.singleNextState(StateFailProceedToRecoverBasic.class, input * 2);
        }else{
            return StateDecision.forceFailWorkflow("unexpected input "+input);
        }
    }
}
