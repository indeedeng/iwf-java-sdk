package io.iworkflow.integ.states;

import io.iworkflow.integ.rpc.DeadEndStateWorkflow;
import io.iworkflow.integ.rpc.NoStartStateWorkflow;
import io.iworkflow.integ.rpc.NoStateWorkflow;
import io.iworkflow.testkit.WorkflowTest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.concurrent.ExecutionException;

public class NoStateTest extends WorkflowTest {

    private static final String RPC_INPUT = "rpc-input";

    public static final Long RPC_OUTPUT = 100L;

    @BeforeEach
    public void setup() throws ExecutionException, InterruptedException {
        super.setup();
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


}
