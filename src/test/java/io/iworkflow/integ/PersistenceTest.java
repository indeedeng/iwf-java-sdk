package io.iworkflow.integ;

import com.google.common.collect.ImmutableMap;
import io.iworkflow.core.Client;
import io.iworkflow.core.ClientOptions;
import io.iworkflow.core.WorkflowOptions;
import io.iworkflow.gen.models.SearchAttribute;
import io.iworkflow.gen.models.SearchAttributeValueType;
import io.iworkflow.integ.persistence.BasicPersistenceWorkflow;
import io.iworkflow.integ.persistence.SetDataAttributeWorkflow;
import io.iworkflow.integ.persistence.SetSearchAttributeWorkflow;
import io.iworkflow.spring.TestSingletonWorkerService;
import io.iworkflow.spring.controller.WorkflowRegistry;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import static io.iworkflow.integ.persistence.BasicPersistenceWorkflow.TEST_SEARCH_ATTRIBUTE_DATE_TIME;
import static io.iworkflow.integ.persistence.BasicPersistenceWorkflow.TEST_SEARCH_ATTRIBUTE_INT;
import static io.iworkflow.integ.persistence.BasicPersistenceWorkflow.TEST_SEARCH_ATTRIBUTE_KEYWORD;
import static io.iworkflow.integ.persistence.SetDataAttributeWorkflow.DATA_OBJECT_KEY;
import static io.iworkflow.integ.persistence.SetDataAttributeWorkflow.DATA_OBJECT_KEY_PREFIX;
import static io.iworkflow.integ.persistence.SetDataAttributeWorkflow.DATA_OBJECT_MODEL_KEY;
import static io.iworkflow.integ.persistence.SetSearchAttributeWorkflow.SEARCH_ATTRIBUTE_BOOL;
import static io.iworkflow.integ.persistence.SetSearchAttributeWorkflow.SEARCH_ATTRIBUTE_DATE_TIME;
import static io.iworkflow.integ.persistence.SetSearchAttributeWorkflow.SEARCH_ATTRIBUTE_DOUBLE;
import static io.iworkflow.integ.persistence.SetSearchAttributeWorkflow.SEARCH_ATTRIBUTE_INT;
import static io.iworkflow.integ.persistence.SetSearchAttributeWorkflow.SEARCH_ATTRIBUTE_KEYWORD;
import static io.iworkflow.integ.persistence.SetSearchAttributeWorkflow.SEARCH_ATTRIBUTE_KEYWORD_ARRAY;
import static io.iworkflow.integ.persistence.SetSearchAttributeWorkflow.SEARCH_ATTRIBUTE_TEXT;

public class PersistenceTest {
    public final static String KEYWORD_VALUE_1 = "keyword-1";
    public final static String KEYWORD_VALUE_2 = "keyword-2";
    public final static String TEXT_VALUE_1 = "text-1";
    public final static Double DOUBLE_VALUE_1 = 01d;
    public final static Long INTEGER_VALUE_1 = 1L;
    public final static Boolean BOOLEAN_VALUE_1 = Boolean.TRUE;
    public final static List<String> ARRAY_STRING_VALUE_1 = Arrays.asList(KEYWORD_VALUE_1, KEYWORD_VALUE_2);
    public final static String DATE_VALUE_1 = "2024-11-12T16:00:01.731455544-08:00";

    @BeforeEach
    public void setup() throws ExecutionException, InterruptedException {
        TestSingletonWorkerService.startWorkerIfNotUp();
    }

    @Test
    public void testPersistenceWorkflow() throws InterruptedException {
        final Client client = new Client(WorkflowRegistry.registry, ClientOptions.localDefault);
        final String wfId = "basic-persistence-test-id" + System.currentTimeMillis() / 1000;
        final String runId = client.startWorkflow(
                BasicPersistenceWorkflow.class, wfId, 10, "start",
                WorkflowOptions.basicBuilder().initialDataAttribute(
                        ImmutableMap.of(BasicPersistenceWorkflow.TEST_INIT_DATA_OBJECT_KEY, "init-test-value"))
                        .build());
        final String output = client.getSimpleWorkflowResultWithWait(String.class, wfId);
        Assertions.assertEquals("test-value-2", output);

        Map<String, Object> map =
                client.getWorkflowDataAttributes(BasicPersistenceWorkflow.class, wfId, runId,
                        Arrays.asList(
                                BasicPersistenceWorkflow.TEST_INIT_DATA_OBJECT_KEY,
                                BasicPersistenceWorkflow.TEST_DATA_OBJECT_KEY,
                                BasicPersistenceWorkflow.TEST_DATA_OBJECT_PREFIX + "1",
                                BasicPersistenceWorkflow.TEST_DATA_OBJECT_PREFIX + "2"));
        Assertions.assertEquals(
                "query-start-query-decide", map.get(BasicPersistenceWorkflow.TEST_DATA_OBJECT_KEY));
        Assertions.assertEquals("init-test-value", map.get(BasicPersistenceWorkflow.TEST_INIT_DATA_OBJECT_KEY));
        Assertions.assertEquals(
                11L, map.get(BasicPersistenceWorkflow.TEST_DATA_OBJECT_PREFIX + "1"));
        Assertions.assertNull(map.get(BasicPersistenceWorkflow.TEST_DATA_OBJECT_PREFIX + "2"));

        // test no runId
        Map<String, Object> map2 =
                client.getWorkflowDataAttributes(BasicPersistenceWorkflow.class, wfId, Arrays.asList(BasicPersistenceWorkflow.TEST_DATA_OBJECT_KEY));
        Assertions.assertEquals(
                "query-start-query-decide", map2.get(BasicPersistenceWorkflow.TEST_DATA_OBJECT_KEY));

        Map<String, Object> allDataAttributes = client.getAllDataAttributes(BasicPersistenceWorkflow.class, wfId, runId);
        Assertions.assertEquals(5, allDataAttributes.size());
        Assertions.assertEquals("query-start-query-decide", allDataAttributes.get(BasicPersistenceWorkflow.TEST_DATA_OBJECT_KEY));
        Assertions.assertEquals(11L, allDataAttributes.get(BasicPersistenceWorkflow.TEST_DATA_OBJECT_PREFIX + "1"));

        // test no runId
        Map<String, Object> allDataAttributes2 = client.getAllDataAttributes(BasicPersistenceWorkflow.class, wfId);
        Assertions.assertEquals(5, allDataAttributes2.size());
        Assertions.assertEquals("query-start-query-decide", allDataAttributes2.get(BasicPersistenceWorkflow.TEST_DATA_OBJECT_KEY));
        Assertions.assertEquals(11L, allDataAttributes.get(BasicPersistenceWorkflow.TEST_DATA_OBJECT_PREFIX + "1"));

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

        client.waitForWorkflowCompletion(wfId);

        final Map<String, Object> finalSearchAttributes = client.getAllSearchAttributes(BasicPersistenceWorkflow.class,
                wfId);

        Assertions.assertEquals(ImmutableMap.builder()
                .put(TEST_SEARCH_ATTRIBUTE_INT, 2L)
                .put(TEST_SEARCH_ATTRIBUTE_KEYWORD, "keyword-2")
                // .put(TEST_SEARCH_ATTRIBUTE_DATE_TIME, "2023-04-17T16:17:49-05:00") // This is a bug. The iwf-server always returns utc time. See https://github.com/indeedeng/iwf/issues/261
                .put(TEST_SEARCH_ATTRIBUTE_DATE_TIME, "2023-04-17T21:17:49Z")
                .build(), finalSearchAttributes);
    }

    @Test
    public void testSetSearchAttributes() {
        final Client client = new Client(WorkflowRegistry.registry, ClientOptions.localDefault);
        final String wfId = "set-search-attribute-test-id" + System.currentTimeMillis() / 1000;
        final String runId = client.startWorkflow(
                SetSearchAttributeWorkflow.class, wfId, 10, "start");

        client.setWorkflowSearchAttributes(SetSearchAttributeWorkflow.class, wfId, Arrays.asList(
                new SearchAttribute()
                    .key(SEARCH_ATTRIBUTE_KEYWORD)
                    .stringValue(KEYWORD_VALUE_1)
                    .valueType(SearchAttributeValueType.KEYWORD),
                new SearchAttribute()
                    .key(SEARCH_ATTRIBUTE_TEXT)
                    .stringValue(TEXT_VALUE_1)
                    .valueType(SearchAttributeValueType.TEXT),
                new SearchAttribute()
                    .key(SEARCH_ATTRIBUTE_DOUBLE)
                    .doubleValue(DOUBLE_VALUE_1)
                    .valueType(SearchAttributeValueType.DOUBLE),
                new SearchAttribute()
                    .key(SEARCH_ATTRIBUTE_INT)
                    .integerValue(INTEGER_VALUE_1)
                    .valueType(SearchAttributeValueType.INT),
                new SearchAttribute()
                    .key(SEARCH_ATTRIBUTE_BOOL)
                    .boolValue(BOOLEAN_VALUE_1)
                    .valueType(SearchAttributeValueType.BOOL),
                new SearchAttribute()
                    .key(SEARCH_ATTRIBUTE_KEYWORD_ARRAY)
                    .stringArrayValue(ARRAY_STRING_VALUE_1)
                    .valueType(SearchAttributeValueType.KEYWORD_ARRAY),
                new SearchAttribute()
                    .key(SEARCH_ATTRIBUTE_DATE_TIME)
                    .stringValue(DATE_VALUE_1)
                    .valueType(SearchAttributeValueType.DATETIME)
        ));

        //Wait for workflow to complete to ensure search attribute values were added
        final String result = client.waitForWorkflowCompletion(String.class, wfId);
        Assertions.assertEquals("test-result", result);

        final Map<String, Object> returnedSearchAttributes = client.getWorkflowSearchAttributes(
                SetSearchAttributeWorkflow.class,
                wfId,
                runId,
                Arrays.asList(
                        SEARCH_ATTRIBUTE_KEYWORD,
                        SEARCH_ATTRIBUTE_TEXT,
                        SEARCH_ATTRIBUTE_DOUBLE,
                        SEARCH_ATTRIBUTE_INT,
                        SEARCH_ATTRIBUTE_BOOL,
                        SEARCH_ATTRIBUTE_KEYWORD_ARRAY,
                        SEARCH_ATTRIBUTE_DATE_TIME));

        final Map<String, Object> expectedSearchAttributes = ImmutableMap.of(
                SEARCH_ATTRIBUTE_KEYWORD, KEYWORD_VALUE_1,
                SEARCH_ATTRIBUTE_TEXT, TEXT_VALUE_1,
                SEARCH_ATTRIBUTE_DOUBLE, DOUBLE_VALUE_1,
                SEARCH_ATTRIBUTE_INT, INTEGER_VALUE_1,
                SEARCH_ATTRIBUTE_BOOL, BOOLEAN_VALUE_1,
                SEARCH_ATTRIBUTE_KEYWORD_ARRAY, ARRAY_STRING_VALUE_1,
                // SEARCH_ATTRIBUTE_DATE_TIME, "2024-11-12T18:00:01.731455544-06:00" //This is a bug. The iwf-server always returns utc time. See https://github.com/indeedeng/iwf/issues/261
                SEARCH_ATTRIBUTE_DATE_TIME, "2024-11-13T00:00:01.731455544Z"
        );
        Assertions.assertEquals(expectedSearchAttributes, returnedSearchAttributes);
    }

    @Test
    public void testSetDataAttributes() {
        final Client client = new Client(WorkflowRegistry.registry, ClientOptions.localDefault);
        final String wfId = "set-data-objects-test-id" + System.currentTimeMillis() / 1000;
        final String runId = client.startWorkflow(
                SetDataAttributeWorkflow.class, wfId, 10, "start");

        final String dataAttributeKeyWithPrefix = DATA_OBJECT_KEY_PREFIX + "1";
        final Map<String, Object> dataAttributes = ImmutableMap.of(
                DATA_OBJECT_KEY, "query-start",
                DATA_OBJECT_MODEL_KEY, new io.iworkflow.gen.models.Context(),
                dataAttributeKeyWithPrefix, 20L);

        client.setWorkflowDataAttributes(
                SetDataAttributeWorkflow.class,
                wfId,
                runId,
                dataAttributes);

        //Wait for workflow to complete to ensure data objects values were added
        final String result = client.waitForWorkflowCompletion(String.class, wfId);
        Assertions.assertEquals("test-result", result);

        final Map<String, Object> actualDataAttributes = client.getWorkflowDataAttributes(
                SetDataAttributeWorkflow.class,
                wfId,
                Arrays.asList(DATA_OBJECT_KEY, DATA_OBJECT_MODEL_KEY, dataAttributeKeyWithPrefix));
        Assertions.assertEquals(dataAttributes, actualDataAttributes);
    }

}
