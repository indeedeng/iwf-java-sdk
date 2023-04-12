package io.iworkflow.integ.interstatechannel;

import io.iworkflow.core.Context;
import io.iworkflow.core.StateDecision;
import io.iworkflow.core.StateMovement;
import io.iworkflow.core.WorkflowState;
import io.iworkflow.core.command.CommandRequest;
import io.iworkflow.core.command.CommandResults;
import io.iworkflow.core.communication.Communication;
import io.iworkflow.core.persistence.Persistence;

public class BasicInterStateChannelWorkflowState0 implements WorkflowState<Integer> {

    @Override
    public Class<Integer> getInputType() {
        return Integer.class;
    }

    @Override
    public CommandRequest waitUntil(
            Context context,
            Integer input,
            Persistence persistence, final Communication communication) {
        return CommandRequest.empty;
    }

    @Override
    public StateDecision execute(
            Context context,
            Integer input,
            CommandResults commandResults,
            Persistence persistence, final Communication communication) {
        return StateDecision.multiNextStates(
                StateMovement.create(BasicInterStateChannelWorkflowState1.class, input),
                StateMovement.create(BasicInterStateChannelWorkflowState2.class, input)
        );
    }
}
