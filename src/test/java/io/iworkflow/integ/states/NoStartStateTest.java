package io.iworkflow.integ.states;

import io.iworkflow.core.Client;
import io.iworkflow.core.ClientOptions;
import io.iworkflow.core.ObjectWorkflow;
import io.iworkflow.integ.rpc.DeadEndStateWorkflow;
import io.iworkflow.integ.rpc.NoStartStateWorkflow;
import io.iworkflow.integ.rpc.NoStateWorkflow;
import io.iworkflow.integ.rpc.RpcWorkflowState2;
import io.iworkflow.spring.TestSingletonWorkerService;
import io.iworkflow.spring.controller.WorkflowRegistry;
import io.iworkflow.testkit.WorkflowTest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.concurrent.ExecutionException;

public class NoStartStateTest extends WorkflowTest {

    private static final String RPC_INPUT = "rpc-input";

    public static final Long RPC_OUTPUT = 100L;

    @BeforeEach
    public void setup() throws ExecutionException, InterruptedException {
        super.setup();
    }

    @Test
    public void testNoStartStateWorkflow() {
        final NoStartStateWorkflow rpcStub = startWorkflowAndReturnStub(
                NoStartStateWorkflow.class,
                "testNoStartStateWorkflow",
                10,
                RPC_OUTPUT-1
        );
        final Long rpcOutput = client.invokeRPC(rpcStub::testRpcFunc1, RPC_INPUT);
        Assertions.assertEquals(RPC_OUTPUT, rpcOutput);
        // output
        client.getSimpleWorkflowResultWithWait(Integer.class, wfId);
        // TODO fix
//        Assertions.assertEquals(1, counter);
    }


    @Test
    public void testNoStateWorkflow() {
        final NoStateWorkflow rpcStub = startWorkflowAndReturnStub(NoStateWorkflow.class,
                "testNoStateWorkflow",
                10,
                RPC_OUTPUT-1
        );
        final Long rpcOutput = client.invokeRPC(rpcStub::testRpcFunc1, RPC_INPUT);
        Assertions.assertEquals(RPC_OUTPUT, rpcOutput);
        client.stopWorkflow(wfId, "");
    }

    @Test
    public void testDeadEndWorkflow() {
        final DeadEndStateWorkflow rpcStub = startWorkflowAndReturnStub(
                DeadEndStateWorkflow.class,
                "testDeadEndWorkflow",
                10
        );
        final Long rpcOutput = client.invokeRPC(rpcStub::testRpcFunc1, RPC_INPUT);
        Assertions.assertEquals(RPC_OUTPUT, rpcOutput);
        Integer out = client.getSimpleWorkflowResultWithWait(Integer.class, wfId);
        Assertions.assertNull(out);
    }

}
