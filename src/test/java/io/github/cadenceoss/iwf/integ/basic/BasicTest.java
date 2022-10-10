package io.github.cadenceoss.iwf.integ.basic;

import io.github.cadenceoss.iwf.core.Client;
import io.github.cadenceoss.iwf.core.Registry;
import io.github.cadenceoss.iwf.core.WorkflowStartOptions;
import io.github.cadenceoss.iwf.core.ClientOptions;
import io.github.cadenceoss.iwf.spring.TestSingletonWorkerService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class BasicTest {

    @BeforeEach
    public void setup() {
        TestSingletonWorkerService.startWorkerIfNotUp();
    }

    @Test
    public void testBasicWorkflow() throws InterruptedException {
        final Registry registry = new Registry();
        final BasicWorkflow wf = new BasicWorkflow();
        registry.addWorkflow(wf);

        final Client client = new Client(registry, ClientOptions.localDefault);
        final String wfId = "basic-test-id" + System.currentTimeMillis() / 1000;
        final WorkflowStartOptions startOptions = WorkflowStartOptions.minimum(10);
        final Integer input = new Integer(0);
        client.StartWorkflow(BasicWorkflow.class, BasicWorkflowS1.StateId, input, wfId, startOptions);
        // wait for workflow to finish
        final Integer output = client.GetSingleWorkflowStateOutputWithLongWait(Integer.class, wfId);
        Assertions.assertEquals(input + 2, output);
    }

    @Test
    public void testBasicSignalWorkflow() throws InterruptedException {
        final Registry registry = new Registry();
        final BasicSignalWorkflow basicSignalWorkflow = new BasicSignalWorkflow();
        registry.addWorkflow(basicSignalWorkflow);

        final Client client = new Client(registry, ClientOptions.localDefault);
        final String wfId = "basic-signal-test-id" + System.currentTimeMillis() / 1000;
        final WorkflowStartOptions startOptions = WorkflowStartOptions.minimum(10);
        final Integer input = Integer.valueOf(0);
        final String runId = client.StartWorkflow(
                BasicSignalWorkflow.class, BasicSignalWorkflowState1.STATE_ID, input, wfId, startOptions);
        client.SignalWorkflow(wfId, runId, BasicSignalWorkflowState1.COMMAND_ID, Integer.valueOf(1));
        final Integer output = client.GetSingleWorkflowStateOutputWithLongWait(Integer.class, wfId);
    }

    @Test
    public void sendSignal() {
        final Registry registry = new Registry();
        final BasicSignalWorkflow basicSignalWorkflow = new BasicSignalWorkflow();
        registry.addWorkflow(basicSignalWorkflow);

        final Client client = new Client(registry, ClientOptions.localDefault);
        client.SignalWorkflow("basic-signal-test-id1665293895", "fef3048c-464d-4cc1-ab6c-d386d0fc4ab1", BasicSignalWorkflowState1.COMMAND_ID, Integer.valueOf(1));

    }
}
