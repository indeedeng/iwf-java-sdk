package io.iworkflow.integ;

import io.iworkflow.core.Client;
import io.iworkflow.core.ClientOptions;
import io.iworkflow.core.ImmutableWorkflowOptions;
import io.iworkflow.core.WorkflowOptions;
import io.iworkflow.gen.models.IDReusePolicy;
import io.iworkflow.integ.stateoptions.StateOptionsWorkflow;
import io.iworkflow.spring.TestSingletonWorkerService;
import io.iworkflow.spring.controller.WorkflowRegistry;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.concurrent.ExecutionException;

public class StateOptionsTest {
    @BeforeEach
    public void setup() throws ExecutionException, InterruptedException {
        TestSingletonWorkerService.startWorkerIfNotUp();
    }
    @Test
    public void testStateOptionsWorkflow() {
        final Client client = new Client(WorkflowRegistry.registry, ClientOptions.localDefault);
        final String wfId = "state-options-test-id" + System.currentTimeMillis() / 1000;
        final WorkflowOptions startOptions = ImmutableWorkflowOptions.builder()
                .workflowIdReusePolicy(IDReusePolicy.DISALLOW_REUSE)
                .build();
        client.startWorkflow(StateOptionsWorkflow.class, wfId, 10, null, startOptions);
        // wait for workflow to finish
        final String output = client.getSimpleWorkflowResultWithWait(String.class, wfId);

        // If no exception thrown, then the test is successful
        Assertions.assertEquals("success", output);
    }
}

