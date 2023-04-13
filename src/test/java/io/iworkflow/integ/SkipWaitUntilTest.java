package io.iworkflow.integ;

import io.iworkflow.core.Client;
import io.iworkflow.core.ClientOptions;
import io.iworkflow.core.ImmutableWorkflowOptions;
import io.iworkflow.core.WorkflowOptions;
import io.iworkflow.gen.models.IDReusePolicy;
import io.iworkflow.gen.models.WorkflowConfig;
import io.iworkflow.integ.basic.SkipWaitUntilWorkflow;
import io.iworkflow.spring.TestSingletonWorkerService;
import io.iworkflow.spring.controller.WorkflowRegistry;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.concurrent.ExecutionException;

public class SkipWaitUntilTest {

    @BeforeEach
    public void setup() throws ExecutionException, InterruptedException {
        TestSingletonWorkerService.startWorkerIfNotUp();
    }

    @Test
    public void testSkipWaitUntil() throws InterruptedException {
        final Client client = new Client(WorkflowRegistry.registry, ClientOptions.localDefault);
        final String wfId = "testSkipWaitUntil-" + System.currentTimeMillis() / 1000;
        final WorkflowOptions startOptions = ImmutableWorkflowOptions.builder()
                .workflowIdReusePolicy(IDReusePolicy.DISALLOW_REUSE)
                .workflowConfigOverride(new WorkflowConfig().continueAsNewThreshold(1))
                .build();
        final int input = 0;
        client.startWorkflow(SkipWaitUntilWorkflow.class, wfId, 10, input, startOptions);
        // wait for workflow to finish
        final Integer output = client.getSimpleWorkflowResultWithWait(Integer.class, wfId);
        Assertions.assertEquals(input + 2, output);
    }
}
