package io.github.cadenceoss.iwf.integ.interstatechannel;

import io.github.cadenceoss.iwf.core.Context;
import io.github.cadenceoss.iwf.core.StateDecision;
import io.github.cadenceoss.iwf.core.WorkflowState;
import io.github.cadenceoss.iwf.core.command.CommandRequest;
import io.github.cadenceoss.iwf.core.command.CommandResults;
import io.github.cadenceoss.iwf.core.communication.Communication;
import io.github.cadenceoss.iwf.core.persistence.Persistence;

public class BasicInterStateChannelWorkflowState2 implements WorkflowState<Integer> {
    public static final String STATE_ID = "inter-state-s2";

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
            Persistence persistence, final Communication communication) {
        communication.publishInterstateChannel(BasicInterStateChannelWorkflow.INTER_STATE_CHANNEL_NAME_1, 2);
        return CommandRequest.empty;
    }

    @Override
    public StateDecision decide(
            Context context,
            Integer input,
            CommandResults commandResults,
            Persistence persistence, final Communication communication) {
        return StateDecision.DEAD_END;
    }
}
