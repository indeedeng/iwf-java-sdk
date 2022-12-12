package io.iworkflow.integ.interstatechannel;

import io.iworkflow.core.Context;
import io.iworkflow.core.StateDecision;
import io.iworkflow.core.WorkflowState;
import io.iworkflow.core.command.CommandRequest;
import io.iworkflow.core.command.CommandResults;
import io.iworkflow.core.communication.Communication;
import io.iworkflow.core.communication.InterStateChannelCommand;
import io.iworkflow.core.communication.InterStateChannelCommandResult;
import io.iworkflow.core.persistence.Persistence;
import io.iworkflow.gen.models.InterStateChannelResult;

public class BasicInterStateChannelWorkflowState1 implements WorkflowState<Integer> {
    public static final String STATE_ID = "inter-state-s1";
    public static final String COMMAND_ID = "test-cmd-id";

    @Override
    public String getStateId() {
        return STATE_ID;
    }

    @Override
    public Class<Integer> getInputType() {
        return Integer.class;
    }

    @Override
    public CommandRequest start(
            Context context,
            Integer input,
            Persistence persistence,
            final Communication communication) {
        return CommandRequest.forAnyCommandCompleted(
                InterStateChannelCommand.create(COMMAND_ID, BasicInterStateChannelWorkflow.INTER_STATE_CHANNEL_NAME_1),
                InterStateChannelCommand.create(COMMAND_ID, BasicInterStateChannelWorkflow.INTER_STATE_CHANNEL_NAME_2)
        );
    }

    @Override
    public StateDecision decide(
            Context context,
            Integer input,
            CommandResults commandResults,
            Persistence persistence,
            final Communication communication) {
        final InterStateChannelCommandResult result1 = commandResults.getAllInterStateChannelCommandResult().get(0);
        Integer output = input + (Integer) result1.getValue().get();

        final InterStateChannelCommandResult result2 = commandResults.getAllInterStateChannelCommandResult().get(1);
        if (result2.getRequestStatusEnum() != InterStateChannelResult.RequestStatusEnum.WAITING) {
            throw new RuntimeException("the second command should be waiting");
        }
        return StateDecision.gracefulCompleteWorkflow(output);
    }
}
