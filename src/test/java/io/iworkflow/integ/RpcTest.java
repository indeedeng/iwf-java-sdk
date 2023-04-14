package io.iworkflow.integ;

import com.google.common.collect.ImmutableMap;
import io.iworkflow.core.Client;
import io.iworkflow.core.ClientOptions;
import io.iworkflow.core.ImmutableStopWorkflowOptions;
import io.iworkflow.gen.models.WorkflowStopType;
import io.iworkflow.integ.persistence.BasicPersistenceWorkflow;
import io.iworkflow.integ.rpc.RpcWorkflow;
import io.iworkflow.integ.rpc.RpcWorkflowState2;
import io.iworkflow.spring.TestSingletonWorkerService;
import io.iworkflow.spring.controller.WorkflowRegistry;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import static io.iworkflow.integ.persistence.BasicPersistenceWorkflow.TEST_SEARCH_ATTRIBUTE_INT;
import static io.iworkflow.integ.persistence.BasicPersistenceWorkflow.TEST_SEARCH_ATTRIBUTE_KEYWORD;
import static io.iworkflow.integ.rpc.RpcWorkflow.TEST_DATA_OBJECT_KEY;

public class RpcTest {

    private static final String RPC_INPUT = "rpc-input";

    public static final Long RPC_OUTPUT = 100L;
    public static final String HARDCODED_STR = "random-string";

    @BeforeEach
    public void setup() throws ExecutionException, InterruptedException {
        TestSingletonWorkerService.startWorkerIfNotUp();
    }

    @Test
    public void testRPCWorkflowFunc1() throws InterruptedException {
        final Client client = new Client(WorkflowRegistry.registry, ClientOptions.localDefault);
        final String wfId = "rpc-test-id" + System.currentTimeMillis() / 1000;
        final String runId = client.startWorkflow(
                RpcWorkflow.class, wfId, 10, 999);

        final RpcWorkflow rpcStub = client.newRpcStub(RpcWorkflow.class, wfId, "");
        final Long rpcOutput = client.invokeRPC(rpcStub::testRpcFunc1, RPC_INPUT);

        Assertions.assertEquals(RPC_OUTPUT, rpcOutput);

        // output
        final Integer output = client.getSimpleWorkflowResultWithWait(Integer.class, wfId);
        RpcWorkflowState2.resetCounter();
        Assertions.assertEquals(2, output);

        // data attrs
        Map<String, Object> dataAttrs =
                client.getWorkflowDataObjects(BasicPersistenceWorkflow.class, wfId, runId, Arrays.asList(BasicPersistenceWorkflow.TEST_DATA_OBJECT_KEY));
        Assertions.assertEquals(
                ImmutableMap.builder()
                        .put(TEST_DATA_OBJECT_KEY, RPC_INPUT)
                        .build(), dataAttrs);

        // search attrs
        final Map<String, Object> searchAttributes = client.getWorkflowSearchAttributes(BasicPersistenceWorkflow.class,
                wfId, "", Arrays.asList(TEST_SEARCH_ATTRIBUTE_KEYWORD, TEST_SEARCH_ATTRIBUTE_INT));

        Assertions.assertEquals(ImmutableMap.builder()
                .put(TEST_SEARCH_ATTRIBUTE_INT, RPC_OUTPUT)
                .put(TEST_SEARCH_ATTRIBUTE_KEYWORD, RPC_INPUT)
                .build(), searchAttributes);
    }

    @Test
    public void testRPCWorkflowFunc0() throws InterruptedException {
        final Client client = new Client(WorkflowRegistry.registry, ClientOptions.localDefault);
        final String wfId = "rpc-test-id" + System.currentTimeMillis() / 1000;
        final String runId = client.startWorkflow(
                RpcWorkflow.class, wfId, 10, 999);

        final RpcWorkflow rpcStub = client.newRpcStub(RpcWorkflow.class, wfId, "");
        final Long rpcOutput = client.invokeRPC(rpcStub::testRpcFunc0);

        Assertions.assertEquals(RPC_OUTPUT, rpcOutput);

        // output
        final Integer output = client.getSimpleWorkflowResultWithWait(Integer.class, wfId);
        RpcWorkflowState2.resetCounter();
        Assertions.assertEquals(2, output);

        // data attrs
        Map<String, Object> dataAttrs =
                client.getWorkflowDataObjects(BasicPersistenceWorkflow.class, wfId, runId, Arrays.asList(BasicPersistenceWorkflow.TEST_DATA_OBJECT_KEY));
        Assertions.assertEquals(
                ImmutableMap.builder()
                        .put(TEST_DATA_OBJECT_KEY, HARDCODED_STR)
                        .build(), dataAttrs);

        // search attrs
        final Map<String, Object> searchAttributes = client.getWorkflowSearchAttributes(BasicPersistenceWorkflow.class,
                wfId, "", Arrays.asList(TEST_SEARCH_ATTRIBUTE_KEYWORD, TEST_SEARCH_ATTRIBUTE_INT));

        Assertions.assertEquals(ImmutableMap.builder()
                .put(TEST_SEARCH_ATTRIBUTE_INT, RPC_OUTPUT)
                .put(TEST_SEARCH_ATTRIBUTE_KEYWORD, HARDCODED_STR)
                .build(), searchAttributes);

    }

    @Test
    public void testRPCWorkflowProc1() throws InterruptedException {
        final Client client = new Client(WorkflowRegistry.registry, ClientOptions.localDefault);
        final String wfId = "rpc-test-id" + System.currentTimeMillis() / 1000;
        final String runId = client.startWorkflow(
                RpcWorkflow.class, wfId, 10, 999);

        final RpcWorkflow rpcStub = client.newRpcStub(RpcWorkflow.class, wfId, "");
        client.invokeRPC(rpcStub::testRpcProc1, RPC_INPUT);

        // output
        final Integer output = client.getSimpleWorkflowResultWithWait(Integer.class, wfId);
        RpcWorkflowState2.resetCounter();
        Assertions.assertEquals(2, output);

        // data attrs
        Map<String, Object> dataAttrs =
                client.getWorkflowDataObjects(BasicPersistenceWorkflow.class, wfId, runId, Arrays.asList(BasicPersistenceWorkflow.TEST_DATA_OBJECT_KEY));
        Assertions.assertEquals(
                ImmutableMap.builder()
                        .put(TEST_DATA_OBJECT_KEY, RPC_INPUT)
                        .build(), dataAttrs);

        // search attrs
        final Map<String, Object> searchAttributes = client.getWorkflowSearchAttributes(BasicPersistenceWorkflow.class,
                wfId, "", Arrays.asList(TEST_SEARCH_ATTRIBUTE_KEYWORD, TEST_SEARCH_ATTRIBUTE_INT));

        Assertions.assertEquals(ImmutableMap.builder()
                .put(TEST_SEARCH_ATTRIBUTE_INT, RPC_OUTPUT)
                .put(TEST_SEARCH_ATTRIBUTE_KEYWORD, RPC_INPUT)
                .build(), searchAttributes);
    }

    @Test
    public void testRPCWorkflowProc0() throws InterruptedException {
        final Client client = new Client(WorkflowRegistry.registry, ClientOptions.localDefault);
        final String wfId = "rpc-test-id" + System.currentTimeMillis() / 1000;
        final String runId = client.startWorkflow(
                RpcWorkflow.class, wfId, 10, 999);

        final RpcWorkflow rpcStub = client.newRpcStub(RpcWorkflow.class, wfId, "");
        client.invokeRPC(rpcStub::testRpcProc0);

        // output
        final Integer output = client.getSimpleWorkflowResultWithWait(Integer.class, wfId);
        RpcWorkflowState2.resetCounter();
        Assertions.assertEquals(2, output);

        // data attrs
        Map<String, Object> dataAttrs =
                client.getWorkflowDataObjects(BasicPersistenceWorkflow.class, wfId, runId, Arrays.asList(BasicPersistenceWorkflow.TEST_DATA_OBJECT_KEY));
        Assertions.assertEquals(
                ImmutableMap.builder()
                        .put(TEST_DATA_OBJECT_KEY, HARDCODED_STR)
                        .build(), dataAttrs);

        // search attrs
        final Map<String, Object> searchAttributes = client.getWorkflowSearchAttributes(BasicPersistenceWorkflow.class,
                wfId, "", Arrays.asList(TEST_SEARCH_ATTRIBUTE_KEYWORD, TEST_SEARCH_ATTRIBUTE_INT));

        Assertions.assertEquals(ImmutableMap.builder()
                .put(TEST_SEARCH_ATTRIBUTE_INT, RPC_OUTPUT)
                .put(TEST_SEARCH_ATTRIBUTE_KEYWORD, HARDCODED_STR)
                .build(), searchAttributes);
    }

    @Test
    public void testRPCWorkflowFunc1ReadOnly() throws InterruptedException {
        final Client client = new Client(WorkflowRegistry.registry, ClientOptions.localDefault);
        final String wfId = "rpc-test-id" + System.currentTimeMillis() / 1000;
        final String runId = client.startWorkflow(
                RpcWorkflow.class, wfId, 10, 999);

        final RpcWorkflow rpcStub = client.newRpcStub(RpcWorkflow.class, wfId, "");
        final Long rpcOutput = client.invokeRPC(rpcStub::testRpcFunc1Readonly, RPC_INPUT);

        Assertions.assertEquals(RPC_OUTPUT, rpcOutput);

        client.stopWorkflow(wfId, "", ImmutableStopWorkflowOptions.builder()
                .workflowStopType(WorkflowStopType.FAIL)
                .reason(HARDCODED_STR)
                .build());

    }

}
