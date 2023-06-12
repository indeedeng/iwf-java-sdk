package io.iworkflow.integ.memo;

import com.google.common.collect.ImmutableMap;
import io.iworkflow.core.Client;
import io.iworkflow.core.ClientOptions;
import io.iworkflow.core.RpcDefinitions;
import io.iworkflow.integ.persistence.BasicPersistenceWorkflow;
import io.iworkflow.integ.memo.RpcMemoWorkflow;
import io.iworkflow.spring.TestSingletonWorkerService;
import io.iworkflow.spring.controller.WorkflowRegistry;
import org.hamcrest.Description;
import org.hamcrest.Matchers;
import org.hamcrest.TypeSafeMatcher;
import org.junit.jupiter.api.*;

import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import static io.iworkflow.integ.persistence.BasicPersistenceWorkflow.TEST_SEARCH_ATTRIBUTE_INT;
import static io.iworkflow.integ.persistence.BasicPersistenceWorkflow.TEST_SEARCH_ATTRIBUTE_KEYWORD;


import static org.hamcrest.Matchers.equalTo;
import static org.awaitility.Awaitility.await;

import static  io.iworkflow.integ.rpc.Keys.*;

public class RpcWithMemoTest {



    public static final Long RPC_OUTPUT = 100L;


    public static final Duration convergenceTimeout = Duration.of(500, ChronoUnit.MILLIS);

    private Client client;

    private RpcMemoWorkflow rpcStub;

    private String wfId;
    private String wfRunId;

    @BeforeEach
    public void setup(TestInfo testInfo) throws ExecutionException, InterruptedException {
        TestSingletonWorkerService.startWorkerIfNotUp();
        this.client = new Client(WorkflowRegistry.registry, ClientOptions.localDefault);
        this.wfId =  testInfo.getDisplayName() + System.currentTimeMillis() / 1000;
        this.wfRunId = client.startWorkflow(
                RpcMemoWorkflow.class, wfId, 60, 999
        );
        this.rpcStub = client.newRpcStub(RpcMemoWorkflow.class, wfId, wfRunId);
        client.invokeRPC(this.rpcStub::initializeStateDataAttributes,2);
    }


    static class IsBlankStringMatcher<T> extends TypeSafeMatcher<T> {
        @Override
        protected boolean matchesSafely(T item) {
            if (item instanceof String) {
                return ((String) item).trim().isEmpty();
            }
            return false;
        }

        @Override
        public void describeTo(Description description) {
            description.appendText("a blank string");
        }
    }

    public <T> void invokeRPCAndAssertConsistency(RpcDefinitions.RpcProc1<T> mutator,
                                                  RpcDefinitions.RpcFunc0<T> stronglyConsistentGetter,
                                                  RpcDefinitions.RpcFunc0<T> weaklyConsistentGetter,
                                                  T value){
        client.invokeRPC(mutator, value);
        T stronglyConsistentValue = client.invokeRPC(stronglyConsistentGetter);
        Assertions.assertEquals(value, stronglyConsistentValue);
        await().atMost(convergenceTimeout).until(
                () -> client.invokeRPC(weaklyConsistentGetter),
                Matchers.anyOf(
                        equalTo(value),
                        new IsBlankStringMatcher<>()
                )
        );
    }


    @Test
    public void testDataAttributesAreEventuallyConsistent() {
        invokeRPCAndAssertConsistency(
                rpcStub::testRpcSetDataAttribute,
                rpcStub::testRpcGetDataAttributeStrongConsistent,
                rpcStub::testRpcGetDataAttribute,
                "test-value"
        );
        invokeRPCAndAssertConsistency(
                rpcStub::testRpcSetDataAttribute,
                rpcStub::testRpcGetDataAttributeStrongConsistent,
                rpcStub::testRpcGetDataAttribute,
                null
        );
    }

    @Test
    public void testKeywordAttributesAreEventuallyConsistent(){
        invokeRPCAndAssertConsistency(
                rpcStub::testRpcSetKeyword,
                rpcStub::testRpcGetKeywordStrongConsistency,
                rpcStub::testRpcGetKeyword,
                "test-value"
        );
        invokeRPCAndAssertConsistency(
                rpcStub::testRpcSetKeyword,
                rpcStub::testRpcGetKeywordStrongConsistency,
                rpcStub::testRpcGetKeyword,
                null
        );
    }

    @Test
    public void testRpcFunc0OutputIsCorrect(){
        Long result = client.invokeRPC(rpcStub::testRpcFunc0);
        Assertions.assertEquals(result, RPC_OUTPUT);
    }


    @Test
    public void testRpcFunc0DataAttributesAreCorrect(){
        client.invokeRPC(rpcStub::testRpcFunc0);
        assertDataAttributesAreCorrect(HARDCODED_STR);
    }

    @Test
    public void testRpcFunc0SearchAttributesAreCorrect(){
        client.invokeRPC(rpcStub::testRpcFunc0);
        assertSearchAttributesAreCorrect(HARDCODED_STR);
    }

    @Test
    public void testRpcProc0OutputIsCorrect(){
        client.invokeRPC(rpcStub::testRpcProc0);
        assertWorkflowResultIsCorrect();
    }

    @Test
    public void testRpcProc0DataAttributesAreCorrect(){
        client.invokeRPC(rpcStub::testRpcProc0);
        assertDataAttributesAreCorrect(HARDCODED_STR);
    }

    @Test
    public void testRpcProc0SearchAttributesAreCorrect(){
        client.invokeRPC(rpcStub::testRpcProc0);
        assertSearchAttributesAreCorrect(HARDCODED_STR);
    }


    @Test
    public void testRpcFunc1OutputIsCorrect(){
        Long result = client.invokeRPC(rpcStub::testRpcFunc1, RPC_INPUT);
        Assertions.assertEquals(result,RPC_OUTPUT);
    }

    @Test
    public void testRpcFunc0WorkflowResultIsCorrect(){
        client.invokeRPC(rpcStub::testRpcFunc0);
        assertWorkflowResultIsCorrect();
    }

    @Test
    public void testRpcFunc1WorkflowResultIsCorrect(){
        client.invokeRPC(rpcStub::testRpcFunc1, RPC_INPUT);
        assertWorkflowResultIsCorrect();
    }

    @Test
    public void testRpcFunc1DataAttributesAreCorrect(){
        client.invokeRPC(rpcStub::testRpcFunc1, RPC_INPUT);
        assertDataAttributesAreCorrect(RPC_INPUT);
    }

    @Test
    public void testRpcFunc1SearchAttributesAreCorrect(){
        client.invokeRPC(rpcStub::testRpcFunc1, RPC_INPUT);
        assertSearchAttributesAreCorrect(RPC_INPUT);
    }

    @Test
    public void testRpcProc1WorkflowResultIsCorrect(){
        client.invokeRPC(rpcStub::testRpcProc1, RPC_INPUT);
        assertWorkflowResultIsCorrect();
    }

    @Test
    public void testRpcProc1DataAttributesAreCorrect(){
        client.invokeRPC(rpcStub::testRpcProc1, RPC_INPUT);
        assertDataAttributesAreCorrect(RPC_INPUT);
    }

    @Test
    public void testRpcProc1SearchAttributesAreCorrect(){
        client.invokeRPC(rpcStub::testRpcProc1, RPC_INPUT);
        assertSearchAttributesAreCorrect(RPC_INPUT);
    }


    public void assertWorkflowResultIsCorrect(){
        Integer output = client.getSimpleWorkflowResultWithWait(Integer.class, wfId,wfRunId);
        Assertions.assertEquals(2, output);
    }


    public void assertDataAttributesAreCorrect(String expectedDataAttributeValue){
        Map<String, Object> dataAttrs =
                client.getWorkflowDataObjects(BasicPersistenceWorkflow.class, wfId, wfRunId, Arrays.asList(BasicPersistenceWorkflow.TEST_DATA_OBJECT_KEY));
        Assertions.assertEquals(
                ImmutableMap.builder()
                        .put(TEST_DATA_OBJECT_KEY, expectedDataAttributeValue)
                        .build(), dataAttrs);
    }


    public void assertSearchAttributesAreCorrect(String expectedSearchAttributeKeyword){
        final Map<String, Object> searchAttributes = client.getWorkflowSearchAttributes(BasicPersistenceWorkflow.class,
                wfId, "", Arrays.asList(TEST_SEARCH_ATTRIBUTE_KEYWORD, TEST_SEARCH_ATTRIBUTE_INT));

        Assertions.assertEquals(ImmutableMap.builder()
                .put(TEST_SEARCH_ATTRIBUTE_INT, RPC_OUTPUT)
                .put(TEST_SEARCH_ATTRIBUTE_KEYWORD, expectedSearchAttributeKeyword)
                .build(), searchAttributes);
    }

}
