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
}
