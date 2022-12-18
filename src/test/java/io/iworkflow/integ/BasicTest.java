package io.iworkflow.integ;

import io.iworkflow.core.Client;
import io.iworkflow.core.ClientOptions;
import io.iworkflow.core.ImmutableWorkflowOptions;
import io.iworkflow.core.WorkflowOptions;
import io.iworkflow.gen.models.WorkflowStartOptions;
import io.iworkflow.integ.basic.BasicWorkflow;
import io.iworkflow.integ.basic.BasicWorkflowState1;
import io.iworkflow.integ.basic.EmptyInputWorkflow;
import io.iworkflow.integ.basic.EmptyInputWorkflowState1;
import io.iworkflow.spring.TestSingletonWorkerService;
import io.iworkflow.spring.controller.WorkflowRegistry;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Optional;
import java.util.concurrent.ExecutionException;

public class BasicTest {

    @BeforeEach
    public void setup() throws ExecutionException, InterruptedException {
        TestSingletonWorkerService.startWorkerIfNotUp();
    }

    @Test
    public void testBasicWorkflow() throws InterruptedException {
        final Client client = new Client(WorkflowRegistry.registry, ClientOptions.localDefault);
        final String wfId = "basic-test-id" + System.currentTimeMillis() / 1000;
        final WorkflowOptions startOptions = ImmutableWorkflowOptions.builder()
                .workflowTimeoutSeconds(10)
                .workflowIdReusePolicy(Optional.of(WorkflowStartOptions.WorkflowIDReusePolicyEnum.ALLOW_DUPLICATE))
                .build();
        final Integer input = 0;
        client.startWorkflow(BasicWorkflow.class, BasicWorkflowState1.StateId, input, wfId, startOptions);
        // wait for workflow to finish
        final Integer output = client.getSimpleWorkflowResultWithWait(Integer.class, wfId);
        Assertions.assertEquals(input + 2, output);
    }

    @Test
    public void testEmptyInputWorkflow() throws InterruptedException {
        final Client client = new Client(WorkflowRegistry.registry, ClientOptions.localDefault);
        final String wfId = "empty-input-test-id" + System.currentTimeMillis() / 1000;
        final WorkflowOptions startOptions = ImmutableWorkflowOptions.builder()
                .workflowTimeoutSeconds(10)
                .workflowIdReusePolicy(Optional.of(io.iworkflow.gen.models.WorkflowStartOptions.WorkflowIDReusePolicyEnum.ALLOW_DUPLICATE))
                .build();
        
        //client.StartWorkflow(EmptyInputWorkflow.class, EmptyInputWorkflowState1.StateId, null, wfId, startOptions);
        client.getUntypedClient().startWorkflow(EmptyInputWorkflow.CUSTOM_WF_TYPE, EmptyInputWorkflowState1.StateId, null, wfId, startOptions);
        // wait for workflow to finish
        client.getSimpleWorkflowResultWithWait(Integer.class, wfId);
    }
}
