package io.github.cadenceoss.iwf.integ.persistence;

import io.github.cadenceoss.iwf.core.Context;
import io.github.cadenceoss.iwf.core.ImmutableStateDecision;
import io.github.cadenceoss.iwf.core.StateDecision;
import io.github.cadenceoss.iwf.core.StateMovement;
import io.github.cadenceoss.iwf.core.WorkflowState;
import io.github.cadenceoss.iwf.core.command.CommandRequest;
import io.github.cadenceoss.iwf.core.command.CommandResults;
import io.github.cadenceoss.iwf.core.communication.Communication;
import io.github.cadenceoss.iwf.core.persistence.Persistence;

import java.util.Arrays;

import static io.github.cadenceoss.iwf.integ.persistence.BasicPersistenceWorkflow.TEST_DATA_OBJECT_KEY;
import static io.github.cadenceoss.iwf.integ.persistence.BasicPersistenceWorkflow.TEST_SEARCH_ATTRIBUTE_INT;
import static io.github.cadenceoss.iwf.integ.persistence.BasicPersistenceWorkflow.TEST_SEARCH_ATTRIBUTE_KEYWORD;

public class BasicPersistenceWorkflowState1 implements WorkflowState<String> {
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
    public CommandRequest start(Context context, String input, Persistence persistence, final Communication communication) {
        persistence.setDataObject(TEST_DATA_OBJECT_KEY, "query-start");
        persistence.setStateLocal("test-key", "test-value-1");
        persistence.recordStateEvent("event-1", "event-1");
        persistence.setSearchAttributeInt64(TEST_SEARCH_ATTRIBUTE_INT, 1L);
        persistence.setSearchAttributeKeyword(TEST_SEARCH_ATTRIBUTE_KEYWORD, "keyword-1");
        return CommandRequest.empty;
    }

    @Override
    public StateDecision decide(Context context, String input, CommandResults commandResults, Persistence persistence, final Communication communication) {
        String str = persistence.getDataObject(TEST_DATA_OBJECT_KEY, String.class);
        persistence.setDataObject(TEST_DATA_OBJECT_KEY, str + "-query-decide");
        String testVal2 = persistence.getStateLocal("test-key", String.class);
        if (testVal2.equals("test-value-1")) {
            persistence.setStateLocal("test-key", "test-value-2");
        }
        persistence.recordStateEvent("event-1", "event-1");
        persistence.recordStateEvent("event-2", "event-2");

        if (persistence.getSearchAttributeInt64(TEST_SEARCH_ATTRIBUTE_INT) == 1L
                && persistence.getSearchAttributeKeyword(TEST_SEARCH_ATTRIBUTE_KEYWORD).equals("keyword-1")) {
            persistence.setSearchAttributeInt64(TEST_SEARCH_ATTRIBUTE_INT, 2L);
            persistence.setSearchAttributeKeyword(TEST_SEARCH_ATTRIBUTE_KEYWORD, "keyword-2");
        }
        return ImmutableStateDecision.builder()
                .nextStates(Arrays.asList(StateMovement.gracefulCompleteWorkflow("test-value-2")))
                .build();
    }
}
