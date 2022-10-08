package io.github.cadenceoss.iwf.integ.basic;

import io.github.cadenceoss.iwf.core.Context;
import io.github.cadenceoss.iwf.core.StateDecision;
import io.github.cadenceoss.iwf.core.WorkflowState;
import io.github.cadenceoss.iwf.core.attributes.QueryAttributesRW;
import io.github.cadenceoss.iwf.core.attributes.SearchAttributesRW;
import io.github.cadenceoss.iwf.core.attributes.StateLocalAttributesR;
import io.github.cadenceoss.iwf.core.attributes.StateLocalAttributesW;
import io.github.cadenceoss.iwf.core.command.CommandRequest;
import io.github.cadenceoss.iwf.core.command.CommandResults;

public class BasicWorkflowS2 implements WorkflowState<Integer> {

    public static final String StateId = "S2";

    @Override
    public String getStateId() {
        return StateId;
    }

    @Override
    public Class<Integer> getInputType() {
        return Integer.class;
    }

    @Override
    public CommandRequest start(final Context context, final Integer input, final StateLocalAttributesW stateLocals, final SearchAttributesRW searchAttributes, final QueryAttributesRW queryAttributes) {
        return CommandRequest.empty;
    }

    @Override
    public StateDecision decide(final Context context, final Integer input, final CommandResults commandResults, final StateLocalAttributesR stateLocals, final SearchAttributesRW searchAttributes, final QueryAttributesRW queryAttributes) {
        final int output = input + 1;
        return StateDecision.gracefulCompleteWorkflow(output);
    }
}
