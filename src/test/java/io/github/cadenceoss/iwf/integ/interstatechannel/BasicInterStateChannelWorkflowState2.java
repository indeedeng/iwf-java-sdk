package io.github.cadenceoss.iwf.integ.interstatechannel;

import io.github.cadenceoss.iwf.core.Context;
import io.github.cadenceoss.iwf.core.StateDecision;
import io.github.cadenceoss.iwf.core.WorkflowState;
import io.github.cadenceoss.iwf.core.command.CommandRequest;
import io.github.cadenceoss.iwf.core.command.CommandResults;
import io.github.cadenceoss.iwf.core.communication.Communication;
import io.github.cadenceoss.iwf.core.persistence.DataObjectsRW;
import io.github.cadenceoss.iwf.core.persistence.SearchAttributesRW;
import io.github.cadenceoss.iwf.core.persistence.StateLocals;

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
            StateLocals stateLocals,
            SearchAttributesRW searchAttributes,
            DataObjectsRW queryAttributes, final Communication communication) {
        communication.publishInterstateChannel(BasicInterStateChannelWorkflow.INTER_STATE_CHANNEL_NAME_1, 2);
        return CommandRequest.empty;
    }

    @Override
    public StateDecision decide(
            Context context,
            Integer input,
            CommandResults commandResults,
            StateLocals stateLocals,
            SearchAttributesRW searchAttributes,
            DataObjectsRW queryAttributes, final Communication communication) {
        return StateDecision.DEAD_END;
    }
}
