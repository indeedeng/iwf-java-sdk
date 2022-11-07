package io.github.cadenceoss.iwf.integ.interstatechannel;

import io.github.cadenceoss.iwf.core.Context;
import io.github.cadenceoss.iwf.core.StateDecision;
import io.github.cadenceoss.iwf.core.WorkflowState;
import io.github.cadenceoss.iwf.core.attributes.QueryAttributesRW;
import io.github.cadenceoss.iwf.core.attributes.SearchAttributesRW;
import io.github.cadenceoss.iwf.core.attributes.StateLocal;
import io.github.cadenceoss.iwf.core.command.CommandRequest;
import io.github.cadenceoss.iwf.core.command.CommandResults;
import io.github.cadenceoss.iwf.core.command.InterStateChannel;

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
            StateLocal stateLocals,
            SearchAttributesRW searchAttributes,
            QueryAttributesRW queryAttributes, final InterStateChannel interStateChannel) {
        interStateChannel.publish(BasicInterStateChannelWorkflow.INTER_STATE_CHANNEL_NAME_1, 2);
        return CommandRequest.empty;
    }

    @Override
    public StateDecision decide(
            Context context,
            Integer input,
            CommandResults commandResults,
            StateLocal stateLocals,
            SearchAttributesRW searchAttributes,
            QueryAttributesRW queryAttributes, final InterStateChannel interStateChannel) {
        return StateDecision.DEAD_END;
    }
}
