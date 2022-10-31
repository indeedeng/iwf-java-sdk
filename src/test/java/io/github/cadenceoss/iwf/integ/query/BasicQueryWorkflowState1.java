package io.github.cadenceoss.iwf.integ.query;

import io.github.cadenceoss.iwf.core.Context;
import io.github.cadenceoss.iwf.core.ImmutableStateDecision;
import io.github.cadenceoss.iwf.core.StateDecision;
import io.github.cadenceoss.iwf.core.StateMovement;
import io.github.cadenceoss.iwf.core.WorkflowState;
import io.github.cadenceoss.iwf.core.attributes.QueryAttributesRW;
import io.github.cadenceoss.iwf.core.attributes.SearchAttributesRW;
import io.github.cadenceoss.iwf.core.attributes.StateLocalAttributesRW;
import io.github.cadenceoss.iwf.core.command.CommandRequest;
import io.github.cadenceoss.iwf.core.command.CommandResults;
import io.github.cadenceoss.iwf.core.command.InterStateChannel;

import java.util.Arrays;

import static io.github.cadenceoss.iwf.integ.query.BasicQueryWorkflow.ATTRIBUTE_KEY;

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
    public CommandRequest start(Context context, String input, StateLocalAttributesRW stateLocals, SearchAttributesRW searchAttributes, QueryAttributesRW queryAttributes, final InterStateChannel interStateChannel) {
        queryAttributes.set(ATTRIBUTE_KEY, "query-start");
        return CommandRequest.empty;
    }

    @Override
    public StateDecision decide(Context context, String input, CommandResults commandResults, StateLocalAttributesRW stateLocals, SearchAttributesRW searchAttributes, QueryAttributesRW queryAttributes, final InterStateChannel interStateChannel) {
        String str = queryAttributes.get(ATTRIBUTE_KEY, String.class);
        queryAttributes.set(ATTRIBUTE_KEY, str + "-query-decide");
        return ImmutableStateDecision.builder()
                .nextStates(Arrays.asList(StateMovement.gracefulCompleteWorkflow("end")))
                .build();
    }
}
