package io.iworkflow.integ;

import io.iworkflow.core.Client;
import io.iworkflow.core.ClientOptions;
import io.iworkflow.integ.conditional.ConditionalCompleteWorkflow;
import io.iworkflow.spring.TestSingletonWorkerService;
import io.iworkflow.spring.controller.WorkflowRegistry;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.concurrent.ExecutionException;

public class ConditionalCompleteTest {

    @BeforeEach
    public void setup() throws ExecutionException, InterruptedException {
        TestSingletonWorkerService.startWorkerIfNotUp();
    }

    @Test
    public void testCompleteIfInternalChannelEmpty() throws InterruptedException {
        testCompleteIfChannelEmpty(false);
    }

    @Test
    public void testCompleteIfSignalChannelEmpty() throws InterruptedException {
        testCompleteIfChannelEmpty(true);
    }

    public void testCompleteIfChannelEmpty(boolean useSignal) throws InterruptedException {
        final Client client = new Client(WorkflowRegistry.registry, ClientOptions.localDefault);
        String namePart;
        if (useSignal) {
            namePart = "Signal";
        } else {
            namePart = "Internal";
        }
        final String wfId = "testCompleteIf" + namePart + "ChannelEmpty" + System.currentTimeMillis() / 1000;
        final String runId = client.startWorkflow(
                ConditionalCompleteWorkflow.class, wfId, 10, useSignal);

        Thread.sleep(1000);

        for (int i = 0; i < 3; i++) {
            if (useSignal) {
                client.signalWorkflow(ConditionalCompleteWorkflow.class, wfId, "", ConditionalCompleteWorkflow.SIGNAL_CHANNEL_NAME, null);
            } else {
                final ConditionalCompleteWorkflow rpcStub = client.newRpcStub(ConditionalCompleteWorkflow.class, wfId, "");
                client.invokeRPC(rpcStub::publishToInternalChannel);
            }
            if (i == 0) {
                // wait for a second so that the workflow is in execute state
                Thread.sleep(1000);
            }
        }

        final Integer output = client.getSimpleWorkflowResultWithWait(Integer.class, wfId);
        Assertions.assertEquals(3, output);

    }
}
