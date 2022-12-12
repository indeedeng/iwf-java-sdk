package io.github.cadenceoss.iwf.integ;

import com.google.common.collect.ImmutableMap;
import io.github.cadenceoss.iwf.core.Client;
import io.github.cadenceoss.iwf.core.ClientOptions;
import io.github.cadenceoss.iwf.core.WorkflowStartOptions;
import io.github.cadenceoss.iwf.integ.persistence.BasicPersistenceWorkflow;
import io.github.cadenceoss.iwf.integ.persistence.BasicPersistenceWorkflowState1;
import io.github.cadenceoss.iwf.spring.TestSingletonWorkerService;
import io.github.cadenceoss.iwf.spring.controller.WorkflowRegistry;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import static io.github.cadenceoss.iwf.integ.persistence.BasicPersistenceWorkflow.TEST_SEARCH_ATTRIBUTE_INT;
import static io.github.cadenceoss.iwf.integ.persistence.BasicPersistenceWorkflow.TEST_SEARCH_ATTRIBUTE_KEYWORD;

public class PersistenceTest {

    @BeforeEach
    public void setup() throws ExecutionException, InterruptedException {
        TestSingletonWorkerService.startWorkerIfNotUp();
    }

    @Test
    public void testPersistenceWorkflow() throws InterruptedException {
        final Client client = new Client(WorkflowRegistry.registry, ClientOptions.localDefault);
        final String wfId = "basic-query-test-id" + System.currentTimeMillis() / 1000;
        final WorkflowStartOptions startOptions = WorkflowStartOptions.minimum(10);
        final String runId = client.startWorkflow(
                BasicPersistenceWorkflow.class, BasicPersistenceWorkflowState1.STATE_ID, "start", wfId, startOptions);
        final String output = client.getSimpleWorkflowResultWithWait(String.class, wfId);
        Map<String, Object> map =
                client.getWorkflowDataObjects(BasicPersistenceWorkflow.class, wfId, runId, Arrays.asList(BasicPersistenceWorkflow.TEST_DATA_OBJECT_KEY));
        Assertions.assertEquals(
                "query-start-query-decide", map.get(BasicPersistenceWorkflow.TEST_DATA_OBJECT_KEY));
        Map<String, Object> allQueryAttributes =
                client.getAllDataObjects(BasicPersistenceWorkflow.class, wfId, runId);
        Assertions.assertEquals(
                "query-start-query-decide", allQueryAttributes.get(BasicPersistenceWorkflow.TEST_DATA_OBJECT_KEY));
        Assertions.assertEquals(
                1, allQueryAttributes.size());
        Assertions.assertEquals("test-value-2", output);

        final Map<String, Object> searchAttributes1 = client.getWorkflowSearchAttributes(BasicPersistenceWorkflow.class,
                wfId, "", Arrays.asList(TEST_SEARCH_ATTRIBUTE_KEYWORD, TEST_SEARCH_ATTRIBUTE_INT));

        final Map<String, Object> searchAttributes2 = client.getAllSearchAttributes(BasicPersistenceWorkflow.class,
                wfId, "");

        Assertions.assertEquals(searchAttributes1, searchAttributes2);
        Assertions.assertEquals(ImmutableMap.builder()
                .put(TEST_SEARCH_ATTRIBUTE_INT, 2L)
                .put(TEST_SEARCH_ATTRIBUTE_KEYWORD, "keyword-2")
                .build(), searchAttributes1);
    }

}
