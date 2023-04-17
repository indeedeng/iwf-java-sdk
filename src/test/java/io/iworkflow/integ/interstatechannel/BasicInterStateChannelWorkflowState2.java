package io.iworkflow.integ.interstatechannel;

import io.iworkflow.core.Context;
import io.iworkflow.core.StateDecision;
import io.iworkflow.core.WorkflowState;
import io.iworkflow.core.command.CommandRequest;
import io.iworkflow.core.command.CommandResults;
import io.iworkflow.core.communication.Communication;
import io.iworkflow.core.persistence.Persistence;

public class BasicInterStateChannelWorkflowState2 implements WorkflowState<Integer> {

    @Override
    public Class<Integer> getInputType() {
        return Integer.class;
    }

    @Override
    public CommandRequest waitUntil(
            Context context,
            Integer input,
            Persistence persistence, final Communication communication) {
        communication.publishInternalChannel(BasicInterStateChannelWorkflow.INTER_STATE_CHANNEL_NAME_1, 2);
        return CommandRequest.empty;
    }

    @Override
    public StateDecision execute(
            Context context,
            Integer input,
            CommandResults commandResults,
            Persistence persistence, final Communication communication) {
        // TODO fix to test StateDecision.deadEnd();
        return StateDecision.gracefulCompleteWorkflow();
    }
}
