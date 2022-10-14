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

import static io.github.cadenceoss.iwf.integ.basic.BasicQueryWorkflow.ATTRIBUTE_KEY;

public class BasicQueryWorkflowState1 implements WorkflowState<String> {
    public static final String STATE_ID = "query-s1";
    @Override
    public String getStateId() {
        return STATE_ID;
    }

    @Override
    public Class<String> getInputType() {
        return String.class;
    }

    @Override
    public CommandRequest start(Context context, String input, StateLocalAttributesW stateLocals, SearchAttributesRW searchAttributes, QueryAttributesRW queryAttributes) {
        queryAttributes.set(ATTRIBUTE_KEY, "modified");
        return CommandRequest.empty;
    }

    @Override
    public StateDecision decide(Context context, String input, CommandResults commandResults, StateLocalAttributesR stateLocals, SearchAttributesRW searchAttributes, QueryAttributesRW queryAttributes) {
        return StateDecision.gracefulCompleteWorkflow("end");
    }
}
