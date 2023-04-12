package io.iworkflow.integ.persistence;

import io.iworkflow.core.Context;
import io.iworkflow.core.ImmutableStateDecision;
import io.iworkflow.core.StateDecision;
import io.iworkflow.core.StateMovement;
import io.iworkflow.core.WorkflowState;
import io.iworkflow.core.command.CommandRequest;
import io.iworkflow.core.command.CommandResults;
import io.iworkflow.core.communication.Communication;
import io.iworkflow.core.persistence.Persistence;
import io.iworkflow.integ.basic.FakContextImpl;

import java.util.Arrays;

import static io.iworkflow.integ.persistence.BasicPersistenceWorkflow.TEST_DATA_OBJECT_KEY;
import static io.iworkflow.integ.persistence.BasicPersistenceWorkflow.TEST_DATA_OBJECT_MODEL_1;
import static io.iworkflow.integ.persistence.BasicPersistenceWorkflow.TEST_DATA_OBJECT_MODEL_2;
import static io.iworkflow.integ.persistence.BasicPersistenceWorkflow.TEST_SEARCH_ATTRIBUTE_INT;
import static io.iworkflow.integ.persistence.BasicPersistenceWorkflow.TEST_SEARCH_ATTRIBUTE_KEYWORD;

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
    public CommandRequest waitUntil(Context context, String input, Persistence persistence, final Communication communication) {
        persistence.setDataAttribute(TEST_DATA_OBJECT_KEY, "query-start");

        // it's allowed to set a child class to a parent
        persistence.setDataAttribute(TEST_DATA_OBJECT_MODEL_1, new FakContextImpl());
        persistence.setDataAttribute(TEST_DATA_OBJECT_MODEL_1, new io.iworkflow.gen.models.Context());

        // but it's not allowed to set a parent to child
        //persistence.setDataObject(TEST_DATA_OBJECT_MODEL_2, new io.iworkflow.gen.models.Context());
        persistence.setDataAttribute(TEST_DATA_OBJECT_MODEL_2, new FakContextImpl());

        persistence.setStateExecutionLocal("test-key", "test-value-1");
        persistence.recordEvent("event-1", "event-1");
        persistence.recordEvent("event-2", "event-1", 2, "event-3");
        persistence.setSearchAttributeInt64(TEST_SEARCH_ATTRIBUTE_INT, 1L);
        persistence.setSearchAttributeKeyword(TEST_SEARCH_ATTRIBUTE_KEYWORD, "keyword-1");
        return CommandRequest.empty;
    }

    @Override
    public StateDecision execute(Context context, String input, CommandResults commandResults, Persistence persistence, final Communication communication) {
        String str = persistence.getDataAttribute(TEST_DATA_OBJECT_KEY, String.class);
        persistence.setDataAttribute(TEST_DATA_OBJECT_KEY, str + "-query-decide");

        // it's not allowed to assign a parent to child
        persistence.getDataAttribute(TEST_DATA_OBJECT_MODEL_1, io.iworkflow.gen.models.Context.class);
        // but it's allowed to assign child to parent
        persistence.getDataAttribute(TEST_DATA_OBJECT_MODEL_2, io.iworkflow.gen.models.Context.class);
        persistence.getDataAttribute(TEST_DATA_OBJECT_MODEL_2, FakContextImpl.class);

        String testVal2 = persistence.getStateExecutionLocal("test-key", String.class);
        if (testVal2.equals("test-value-1")) {
            persistence.setStateExecutionLocal("test-key", "test-value-2");
        }
        persistence.recordEvent("event-1", "event-1");
        persistence.recordEvent("event-2", "event-2");

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
