package io.github.cadenceoss.iwf.integ;

import com.google.common.collect.ImmutableMap;
import io.github.cadenceoss.iwf.core.Client;
import io.github.cadenceoss.iwf.core.ClientOptions;
import io.github.cadenceoss.iwf.core.WorkflowStartOptions;
import io.github.cadenceoss.iwf.integ.attribute.BasicAttributeWorkflow;
import io.github.cadenceoss.iwf.integ.attribute.BasicAttributeWorkflowState1;
import io.github.cadenceoss.iwf.spring.TestSingletonWorkerService;
import io.github.cadenceoss.iwf.spring.controller.WorkflowRegistry;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import static io.github.cadenceoss.iwf.integ.attribute.BasicAttributeWorkflow.TEST_SEARCH_ATTRIBUTE_INT;
import static io.github.cadenceoss.iwf.integ.attribute.BasicAttributeWorkflow.TEST_SEARCH_ATTRIBUTE_KEYWORD;

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
        final String runId = client.StartWorkflow(
                BasicAttributeWorkflow.class, BasicAttributeWorkflowState1.STATE_ID, "start", wfId, startOptions);
        final String output = client.GetSimpleWorkflowResultWithWait(String.class, wfId);
        Map<String, Object> map =
                client.GetWorkflowQueryAttributes(BasicAttributeWorkflow.class, wfId, runId, Arrays.asList(BasicAttributeWorkflow.TEST_DATA_OBJECT_KEY));
        Assertions.assertEquals(
                "query-start-query-decide", map.get(BasicAttributeWorkflow.TEST_DATA_OBJECT_KEY));
        Map<String, Object> allQueryAttributes =
                client.GetAllQueryAttributes(BasicAttributeWorkflow.class, wfId, runId);
        Assertions.assertEquals(
                "query-start-query-decide", allQueryAttributes.get(BasicAttributeWorkflow.TEST_DATA_OBJECT_KEY));
        Assertions.assertEquals(
                1, allQueryAttributes.size());
        Assertions.assertEquals("test-value-2", output);

        final Map<String, Object> searchAttributes1 = client.GetWorkflowSearchAttributes(BasicAttributeWorkflow.class,
                wfId, "", Arrays.asList(TEST_SEARCH_ATTRIBUTE_KEYWORD, TEST_SEARCH_ATTRIBUTE_INT));

        final Map<String, Object> searchAttributes2 = client.GetAllSearchAttributes(BasicAttributeWorkflow.class,
                wfId, "");

        Assertions.assertEquals(searchAttributes1, searchAttributes2);
        Assertions.assertEquals(ImmutableMap.builder()
                .put(TEST_SEARCH_ATTRIBUTE_INT, 2L)
                .put(TEST_SEARCH_ATTRIBUTE_KEYWORD, "keyword-2")
                .build(), searchAttributes1);
    }

}
