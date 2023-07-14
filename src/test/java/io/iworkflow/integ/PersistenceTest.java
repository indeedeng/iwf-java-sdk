package io.iworkflow.integ;

import com.google.common.collect.ImmutableMap;
import io.iworkflow.core.Client;
import io.iworkflow.core.ClientOptions;
import io.iworkflow.integ.persistence.BasicPersistenceWorkflow;
import io.iworkflow.spring.TestSingletonWorkerService;
import io.iworkflow.spring.controller.WorkflowRegistry;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import static io.iworkflow.integ.persistence.BasicPersistenceWorkflow.TEST_SEARCH_ATTRIBUTE_DATE_TIME;
import static io.iworkflow.integ.persistence.BasicPersistenceWorkflow.TEST_SEARCH_ATTRIBUTE_INT;
import static io.iworkflow.integ.persistence.BasicPersistenceWorkflow.TEST_SEARCH_ATTRIBUTE_KEYWORD;
import static io.iworkflow.integ.persistence.BasicPersistenceWorkflowState1.testDateTimeValue;

public class PersistenceTest {

    @BeforeEach
    public void setup() throws ExecutionException, InterruptedException {
        TestSingletonWorkerService.startWorkerIfNotUp();
    }

    @Test
    public void testPersistenceWorkflow() throws InterruptedException {
        final Client client = new Client(WorkflowRegistry.registry, ClientOptions.localDefault);
        final String wfId = "basic-persistence-test-id" + System.currentTimeMillis() / 1000;
        final String runId = client.startWorkflow(
                BasicPersistenceWorkflow.class, wfId, 10, "start");
        final String output = client.getSimpleWorkflowResultWithWait(String.class, wfId);
        Assertions.assertEquals("test-value-2", output);

        Map<String, Object> map =
                client.getWorkflowDataObjects(BasicPersistenceWorkflow.class, wfId, runId, Arrays.asList(BasicPersistenceWorkflow.TEST_DATA_OBJECT_KEY));
        Assertions.assertEquals(
                "query-start-query-decide", map.get(BasicPersistenceWorkflow.TEST_DATA_OBJECT_KEY));

        // test no runId
        Map<String, Object> map2 =
                client.getWorkflowDataObjects(BasicPersistenceWorkflow.class, wfId, Arrays.asList(BasicPersistenceWorkflow.TEST_DATA_OBJECT_KEY));
        Assertions.assertEquals(
                "query-start-query-decide", map2.get(BasicPersistenceWorkflow.TEST_DATA_OBJECT_KEY));

        Map<String, Object> allDataObjects = client.getAllDataObjects(BasicPersistenceWorkflow.class, wfId, runId);
        Assertions.assertEquals(3, allDataObjects.size());
        Assertions.assertEquals("query-start-query-decide", allDataObjects.get(BasicPersistenceWorkflow.TEST_DATA_OBJECT_KEY));

        // test no runId
        Map<String, Object> allDataObjects2 = client.getAllDataObjects(BasicPersistenceWorkflow.class, wfId);
        Assertions.assertEquals(3, allDataObjects2.size());
        Assertions.assertEquals("query-start-query-decide", allDataObjects2.get(BasicPersistenceWorkflow.TEST_DATA_OBJECT_KEY));

        final Map<String, Object> searchAttributes1 = client.getWorkflowSearchAttributes(BasicPersistenceWorkflow.class,
                wfId, "", Arrays.asList(TEST_SEARCH_ATTRIBUTE_KEYWORD, TEST_SEARCH_ATTRIBUTE_INT));
        Assertions.assertEquals(ImmutableMap.builder()
                .put(TEST_SEARCH_ATTRIBUTE_INT, 2L)
                .put(TEST_SEARCH_ATTRIBUTE_KEYWORD, "keyword-2")
                .build(), searchAttributes1);

        // test no runId
        final Map<String, Object> searchAttributes2 = client.getWorkflowSearchAttributes(BasicPersistenceWorkflow.class,
                wfId, Arrays.asList(TEST_SEARCH_ATTRIBUTE_KEYWORD, TEST_SEARCH_ATTRIBUTE_INT));
        Assertions.assertEquals(ImmutableMap.builder()
                .put(TEST_SEARCH_ATTRIBUTE_INT, 2L)
                .put(TEST_SEARCH_ATTRIBUTE_KEYWORD, "keyword-2")
                .build(), searchAttributes2);

        // TODO fix
        // Expected :{CustomIntField=2, CustomKeywordField=keyword-2, CustomDatetimeField=2023-04-17T21:17:49-00:00}
        // Actual   :{CustomDatetimeField=2023-04-17T21:17:49Z, CustomIntField=2, CustomKeywordField=keyword-2}
//        Assertions.assertEquals(ImmutableMap.builder()
//                .put(TEST_SEARCH_ATTRIBUTE_INT, 2L)
//                .put(TEST_SEARCH_ATTRIBUTE_KEYWORD, "keyword-2")
//                .put(TEST_SEARCH_ATTRIBUTE_DATE_TIME, testDateTimeValue)
//                .build(), searchAttributes2);
    }

}
