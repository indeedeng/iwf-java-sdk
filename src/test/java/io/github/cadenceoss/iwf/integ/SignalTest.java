package io.github.cadenceoss.iwf.integ;

import io.github.cadenceoss.iwf.core.Client;
import io.github.cadenceoss.iwf.core.ClientOptions;
import io.github.cadenceoss.iwf.core.WorkflowStartOptions;
import io.github.cadenceoss.iwf.integ.signal.BasicSignalWorkflow;
import io.github.cadenceoss.iwf.integ.signal.BasicSignalWorkflowState1;
import io.github.cadenceoss.iwf.spring.TestSingletonWorkerService;
import io.github.cadenceoss.iwf.spring.controller.WorkflowRegistry;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.concurrent.ExecutionException;

public class SignalTest {

    @BeforeEach
    public void setup() throws ExecutionException, InterruptedException {
        TestSingletonWorkerService.startWorkerIfNotUp();
    }

    @Test
    public void testBasicSignalWorkflow() throws InterruptedException {
        final Client client = new Client(WorkflowRegistry.registry, ClientOptions.localDefault);
        final String wfId = "basic-signal-test-id" + System.currentTimeMillis() / 1000;
        final WorkflowStartOptions startOptions = WorkflowStartOptions.minimum(10);
        final Integer input = 1;
        final String runId = client.StartWorkflow(
                BasicSignalWorkflow.class, BasicSignalWorkflowState1.STATE_ID, input, wfId, startOptions);
        client.SignalWorkflow(
                BasicSignalWorkflow.class, wfId, runId, BasicSignalWorkflowState1.SIGNAL_CHANNEL_NAME_1, Integer.valueOf(2));
        final Integer output = client.GetSimpleWorkflowResultWithWait(Integer.class, wfId);
        Assertions.assertEquals(3, output);
    }
}
