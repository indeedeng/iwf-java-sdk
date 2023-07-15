package io.iworkflow.integ;

import io.iworkflow.core.Client;
import io.iworkflow.core.ClientOptions;
import io.iworkflow.integ.rpc.DeadEndStateWorkflow;
import io.iworkflow.integ.rpc.NoStartStateWorkflow;
import io.iworkflow.integ.rpc.NoStateWorkflow;
import io.iworkflow.integ.rpc.RpcWorkflowState2;
import io.iworkflow.spring.TestSingletonWorkerService;
import io.iworkflow.spring.controller.WorkflowRegistry;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.concurrent.ExecutionException;

public class NoStartStateTest {

    private static final String RPC_INPUT = "rpc-input";

    public static final Long RPC_OUTPUT = 100L;
    public static final String HARDCODED_STR = "random-string";

    @BeforeEach
    public void setup() throws ExecutionException, InterruptedException {
        TestSingletonWorkerService.startWorkerIfNotUp();
    }

    @Test
    public void testNoStartStateWorkflow() throws InterruptedException {
        final Client client = new Client(WorkflowRegistry.registry, ClientOptions.localDefault);
        final String wfId = "testNoStartStateWorkflow" + System.currentTimeMillis() / 1000;
        client.startWorkflow(
                NoStartStateWorkflow.class, wfId, 10, 999);

        final NoStartStateWorkflow rpcStub = client.newRpcStub(NoStartStateWorkflow.class, wfId, "");
        final Long rpcOutput = client.invokeRPC(rpcStub::testRpcFunc1, RPC_INPUT);

        Assertions.assertEquals(RPC_OUTPUT, rpcOutput);

        // output
        client.getSimpleWorkflowResultWithWait(Integer.class, wfId);
        final int counter = RpcWorkflowState2.resetCounter();
        // TODO fix
//        Assertions.assertEquals(1, counter);
    }

    @Test
    public void testNoStateWorkflow() throws InterruptedException {
        final Client client = new Client(WorkflowRegistry.registry, ClientOptions.localDefault);
        final String wfId = "testNoStateWorkflow" + System.currentTimeMillis() / 1000;
        client.startWorkflow(
                NoStateWorkflow.class, wfId, 10, 999);

        final NoStateWorkflow rpcStub = client.newRpcStub(NoStateWorkflow.class, wfId, "");
        final Long rpcOutput = client.invokeRPC(rpcStub::testRpcFunc1, RPC_INPUT);

        Assertions.assertEquals(RPC_OUTPUT, rpcOutput);

        client.stopWorkflow(wfId, null);
    }

    @Test
    public void testDeadEndWorkflow() throws InterruptedException {
        final Client client = new Client(WorkflowRegistry.registry, ClientOptions.localDefault);
        final String wfId = "testDeadEndWorkflow" + System.currentTimeMillis() / 1000;
        client.startWorkflow(
                DeadEndStateWorkflow.class, wfId, 10);

        Thread.sleep(2000);
        final DeadEndStateWorkflow rpcStub = client.newRpcStub(DeadEndStateWorkflow.class, wfId, "");
        final Long rpcOutput = client.invokeRPC(rpcStub::testRpcFunc1, RPC_INPUT);
        RpcWorkflowState2.resetCounter();

        Assertions.assertEquals(RPC_OUTPUT, rpcOutput);

        Integer out = client.getSimpleWorkflowResultWithWait(Integer.class, wfId);
        Assertions.assertNull(out);
    }

}
