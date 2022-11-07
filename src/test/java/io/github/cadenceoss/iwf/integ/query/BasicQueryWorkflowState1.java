package io.github.cadenceoss.iwf.integ.query;

import io.github.cadenceoss.iwf.core.Context;
import io.github.cadenceoss.iwf.core.ImmutableStateDecision;
import io.github.cadenceoss.iwf.core.StateDecision;
import io.github.cadenceoss.iwf.core.StateMovement;
import io.github.cadenceoss.iwf.core.WorkflowState;
import io.github.cadenceoss.iwf.core.attributes.QueryAttributesRW;
import io.github.cadenceoss.iwf.core.attributes.SearchAttributesRW;
import io.github.cadenceoss.iwf.core.attributes.StateLocal;
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
    public CommandRequest start(Context context, String input, StateLocal stateLocals, SearchAttributesRW searchAttributes, QueryAttributesRW queryAttributes, final InterStateChannel interStateChannel) {
        queryAttributes.set(ATTRIBUTE_KEY, "query-start");
        stateLocals.setLocalAttribute("test-key", "test-value-1");
        stateLocals.recordEvent("event-1", "event-1");
        return CommandRequest.empty;
    }

    @Override
    public StateDecision decide(Context context, String input, CommandResults commandResults, StateLocal stateLocals, SearchAttributesRW searchAttributes, QueryAttributesRW queryAttributes, final InterStateChannel interStateChannel) {
        String str = queryAttributes.get(ATTRIBUTE_KEY, String.class);
        queryAttributes.set(ATTRIBUTE_KEY, str + "-query-decide");
        String testVal2 = stateLocals.getLocalAttribute("test-key", String.class);
        if (testVal2.equals("test-value-1")) {
            stateLocals.setLocalAttribute("test-key", "test-value-2");
        }
        stateLocals.recordEvent("event-1", "event-1");
        stateLocals.recordEvent("event-2", "event-2");
        return ImmutableStateDecision.builder()
                .nextStates(Arrays.asList(StateMovement.gracefulCompleteWorkflow("test-value-2")))
                .build();
    }
}
