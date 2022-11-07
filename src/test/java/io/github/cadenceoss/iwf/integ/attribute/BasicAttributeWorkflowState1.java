package io.github.cadenceoss.iwf.integ.attribute;

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

import static io.github.cadenceoss.iwf.integ.attribute.BasicAttributeWorkflow.TEST_QUERY_ATTRIBUTE_KEY;
import static io.github.cadenceoss.iwf.integ.attribute.BasicAttributeWorkflow.TEST_SEARCH_ATTRIBUTE_INT;
import static io.github.cadenceoss.iwf.integ.attribute.BasicAttributeWorkflow.TEST_SEARCH_ATTRIBUTE_KEYWORD;

public class BasicAttributeWorkflowState1 implements WorkflowState<String> {
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
        queryAttributes.set(TEST_QUERY_ATTRIBUTE_KEY, "query-start");
        stateLocals.setLocalAttribute("test-key", "test-value-1");
        stateLocals.recordEvent("event-1", "event-1");
        searchAttributes.setInt64(TEST_SEARCH_ATTRIBUTE_INT, 1L);
        searchAttributes.setKeyword(TEST_SEARCH_ATTRIBUTE_KEYWORD, "keyword-1");
        return CommandRequest.empty;
    }

    @Override
    public StateDecision decide(Context context, String input, CommandResults commandResults, StateLocal stateLocals, SearchAttributesRW searchAttributes, QueryAttributesRW queryAttributes, final InterStateChannel interStateChannel) {
        String str = queryAttributes.get(TEST_QUERY_ATTRIBUTE_KEY, String.class);
        queryAttributes.set(TEST_QUERY_ATTRIBUTE_KEY, str + "-query-decide");
        String testVal2 = stateLocals.getLocalAttribute("test-key", String.class);
        if (testVal2.equals("test-value-1")) {
            stateLocals.setLocalAttribute("test-key", "test-value-2");
        }
        stateLocals.recordEvent("event-1", "event-1");
        stateLocals.recordEvent("event-2", "event-2");

        if (searchAttributes.getInt64(TEST_SEARCH_ATTRIBUTE_INT) == 1L
                && searchAttributes.getKeyword(TEST_SEARCH_ATTRIBUTE_KEYWORD).equals("keyword-1")) {
            searchAttributes.setInt64(TEST_SEARCH_ATTRIBUTE_INT, 2L);
            searchAttributes.setKeyword(TEST_SEARCH_ATTRIBUTE_KEYWORD, "keyword-2");
        }
        return ImmutableStateDecision.builder()
                .nextStates(Arrays.asList(StateMovement.gracefulCompleteWorkflow("test-value-2")))
                .build();
    }
}
