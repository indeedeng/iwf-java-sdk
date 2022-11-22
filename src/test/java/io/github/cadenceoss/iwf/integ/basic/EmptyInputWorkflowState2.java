package io.github.cadenceoss.iwf.integ.basic;

import io.github.cadenceoss.iwf.core.Context;
import io.github.cadenceoss.iwf.core.StateDecision;
import io.github.cadenceoss.iwf.core.WorkflowState;
import io.github.cadenceoss.iwf.core.attributes.QueryAttributesRW;
import io.github.cadenceoss.iwf.core.attributes.SearchAttributesRW;
import io.github.cadenceoss.iwf.core.attributes.StateLocal;
import io.github.cadenceoss.iwf.core.command.CommandRequest;
import io.github.cadenceoss.iwf.core.command.CommandResults;
import io.github.cadenceoss.iwf.core.command.InterStateChannel;

public class EmptyInputWorkflowState2 implements WorkflowState<Void> {

    public static final String StateId = "S2";

    @Override
    public String getStateId() {
        return StateId;
    }

    @Override
    public Class<Void> getInputType() {
        return Void.class;
    }

    @Override
    public CommandRequest start(final Context context, final Void input, final StateLocal stateLocals, final SearchAttributesRW searchAttributes, final QueryAttributesRW queryAttributes, final InterStateChannel interStateChannel) {
        return CommandRequest.empty;
    }

    @Override
    public StateDecision decide(final Context context, final Void input, final CommandResults commandResults, final StateLocal stateLocals, final SearchAttributesRW searchAttributes, final QueryAttributesRW queryAttributes, final InterStateChannel interStateChannel) {
        return StateDecision.gracefulCompleteWorkflow();
    }
}
