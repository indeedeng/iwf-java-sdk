package io.iworkflow.integ;

import io.iworkflow.core.Client;
import io.iworkflow.core.ClientOptions;
import io.iworkflow.core.exceptions.WorkflowNotExistsOrOpenException;
import io.iworkflow.gen.models.ErrorSubStatus;
import io.iworkflow.integ.signal.BasicSignalWorkflow;
import io.iworkflow.integ.signal.BasicSignalWorkflowState2;
import io.iworkflow.integ.signal.MultipleSameSignalWorkflow;
import static io.iworkflow.integ.signal.MultipleSameSignalWorkflow.SIGNAL_NAME_1;
import io.iworkflow.spring.TestSingletonWorkerService;
import io.iworkflow.spring.controller.WorkflowRegistry;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.concurrent.ExecutionException;

import static io.iworkflow.integ.signal.BasicSignalWorkflow.SIGNAL_CHANNEL_NAME_1;
import static io.iworkflow.integ.signal.BasicSignalWorkflow.SIGNAL_CHANNEL_NAME_3;
import static io.iworkflow.integ.signal.BasicSignalWorkflow.SIGNAL_CHANNEL_PREFIX_1;
import static io.iworkflow.integ.signal.BasicSignalWorkflowState2.TIMER_COMMAND_ID;

public class SignalTest {

    @BeforeEach
    public void setup() throws ExecutionException, InterruptedException {
        TestSingletonWorkerService.startWorkerIfNotUp();
    }

    @Test
    public void testBasicSignalWorkflow() throws InterruptedException {
        final Client client = new Client(WorkflowRegistry.registry, ClientOptions.localDefault);
        final String wfId = "basic-signal-test-id" + System.currentTimeMillis() / 1000;
        final Integer input = 1;
        final String runId = client.startWorkflow(
                BasicSignalWorkflow.class, wfId, 10, input);
        client.signalWorkflow(
                BasicSignalWorkflow.class, wfId, runId, SIGNAL_CHANNEL_NAME_1, Integer.valueOf(2));

        client.signalWorkflow(
                BasicSignalWorkflow.class, wfId, runId, SIGNAL_CHANNEL_NAME_1, Integer.valueOf(3));

        // test no runId
        client.signalWorkflow(
                BasicSignalWorkflow.class, wfId, SIGNAL_CHANNEL_NAME_1, Integer.valueOf(5));

        // test sending null signal
        client.signalWorkflow(
                BasicSignalWorkflow.class, wfId, runId, SIGNAL_CHANNEL_NAME_3, null);

        // create by index
        client.signalWorkflow(
                BasicSignalWorkflow.class, wfId, runId, SIGNAL_CHANNEL_PREFIX_1 + "1", Integer.valueOf(4));

        Thread.sleep(1000);// wait for timer to be ready to skip
        client.skipTimer(wfId, "", BasicSignalWorkflowState2.class, 1, TIMER_COMMAND_ID);

        checkWorkflowResultAfterComplete(client, wfId, runId);

        try {
            client.signalWorkflow(
                    BasicSignalWorkflow.class, wfId, runId, SIGNAL_CHANNEL_NAME_1, Integer.valueOf(2));
        } catch (WorkflowNotExistsOrOpenException e) {
            Assertions.assertEquals(ErrorSubStatus.WORKFLOW_NOT_EXISTS_SUB_STATUS, e.getErrorSubStatus());
            Assertions.assertEquals(400, e.getStatusCode());
            return;
        }
        Assertions.fail("signal closed workflow should fail");
    }

    @Test
    public void testMultipleSameSignalWorkflow() {
        final Client client = new Client(WorkflowRegistry.registry, ClientOptions.localDefault);
        final String wfId = "multiple-same-signal-test-id" + System.currentTimeMillis() / 1000;
        final Integer input = 1;
        final String runId = client.startWorkflow(
                MultipleSameSignalWorkflow.class, wfId, 10, input);
        client.signalWorkflow(
                MultipleSameSignalWorkflow.class, wfId, runId, SIGNAL_NAME_1, Integer.valueOf(2));

        client.signalWorkflow(
                MultipleSameSignalWorkflow.class, wfId, runId, SIGNAL_NAME_1, Integer.valueOf(3));

        final Integer output = client.getSimpleWorkflowResultWithWait(Integer.class, wfId);
        Assertions.assertEquals(5, output);
    }

    private void checkWorkflowResultAfterComplete(final Client client, final String wfId, final String runId) {
        Assertions.assertEquals(6, client.getSimpleWorkflowResultWithWait(Integer.class, wfId, runId));
        Assertions.assertEquals(6, client.getSimpleWorkflowResultWithWait(Integer.class, wfId));
        Assertions.assertEquals(6, client.tryGettingSimpleWorkflowResult(Integer.class, wfId, runId));
        Assertions.assertEquals(6, client.tryGettingSimpleWorkflowResult(Integer.class, wfId));

        Assertions.assertEquals(1, client.getComplexWorkflowResultWithWait(wfId, runId).size());
        Assertions.assertEquals(1, client.getComplexWorkflowResultWithWait(wfId).size());
        Assertions.assertEquals(1, client.tryGettingComplexWorkflowResult(wfId, runId).size());
        Assertions.assertEquals(1, client.tryGettingComplexWorkflowResult(wfId).size());
    }
}
