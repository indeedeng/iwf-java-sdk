package io.github.cadenceoss.iwf.integ.interstatechannel;

import io.github.cadenceoss.iwf.core.Context;
import io.github.cadenceoss.iwf.core.StateDecision;
import io.github.cadenceoss.iwf.core.WorkflowState;
import io.github.cadenceoss.iwf.core.command.CommandRequest;
import io.github.cadenceoss.iwf.core.command.CommandResults;
import io.github.cadenceoss.iwf.core.communication.Communication;
import io.github.cadenceoss.iwf.core.communication.InterStateChannelCommand;
import io.github.cadenceoss.iwf.core.communication.InterStateChannelCommandResult;
import io.github.cadenceoss.iwf.core.persistence.Persistence;
import io.github.cadenceoss.iwf.gen.models.InterStateChannelResult;

import static io.github.cadenceoss.iwf.integ.interstatechannel.BasicInterStateChannelWorkflow.INTER_STATE_CHANNEL_NAME_1;
import static io.github.cadenceoss.iwf.integ.interstatechannel.BasicInterStateChannelWorkflow.INTER_STATE_CHANNEL_NAME_2;

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
                InterStateChannelCommand.create(COMMAND_ID, INTER_STATE_CHANNEL_NAME_1),
                InterStateChannelCommand.create(COMMAND_ID, INTER_STATE_CHANNEL_NAME_2)
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
