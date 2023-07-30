package io.iworkflow.integ.internalchannel;

import io.iworkflow.core.Context;
import io.iworkflow.core.StateDecision;
import io.iworkflow.core.WorkflowState;
import io.iworkflow.core.command.CommandRequest;
import io.iworkflow.core.command.CommandResults;
import io.iworkflow.core.communication.Communication;
import io.iworkflow.core.persistence.Persistence;

public class BasicInternalChannelWorkflowState2 implements WorkflowState<Integer> {

    @Override
    public Class<Integer> getInputType() {
        return Integer.class;
    }

    @Override
    public CommandRequest waitUntil(
            Context context,
            Integer input,
            Persistence persistence, final Communication communication) {
        communication.publishInternalChannel(BasicInternalChannelWorkflow.INTER_STATE_CHANNEL_NAME_1, 2);
        communication.publishInternalChannel(BasicInternalChannelWorkflow.INTER_STATE_CHANNEL_PREFIX_1 + "1", 3);
        return CommandRequest.empty;
    }

    @Override
    public StateDecision execute(
            Context context,
            Integer input,
            CommandResults commandResults,
            Persistence persistence, final Communication communication) {
        // TODO fix to test StateDecision.deadEnd(); and then use stop to complete
        //return StateDecision.deadEnd();
         return StateDecision.gracefulCompleteWorkflow();
    }
}
