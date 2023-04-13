package io.iworkflow.integ;

import io.iworkflow.core.Client;
import io.iworkflow.core.ClientOptions;
import io.iworkflow.core.ClientSideException;
import io.iworkflow.core.ImmutableUnregisteredWorkflowOptions;
import io.iworkflow.core.ImmutableWorkflowOptions;
import io.iworkflow.core.UnregisteredWorkflowOptions;
import io.iworkflow.core.WorkflowDefinitionException;
import io.iworkflow.core.WorkflowOptions;
import io.iworkflow.gen.models.Context;
import io.iworkflow.gen.models.ErrorSubStatus;
import io.iworkflow.gen.models.IDReusePolicy;
import io.iworkflow.gen.models.WorkflowConfig;
import io.iworkflow.integ.basic.BasicWorkflow;
import io.iworkflow.integ.basic.EmptyInputWorkflow;
import io.iworkflow.integ.basic.EmptyInputWorkflowState1;
import io.iworkflow.integ.basic.FakContextImpl;
import io.iworkflow.integ.basic.ModelInputWorkflow;
import io.iworkflow.integ.basic.ProceedOnStateStartFailWorkflow;
import io.iworkflow.spring.TestSingletonWorkerService;
import io.iworkflow.spring.controller.WorkflowRegistry;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

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
                .workflowIdReusePolicy(IDReusePolicy.DISALLOW_REUSE)
                .build();
        final Integer input = 0;
        client.startWorkflow(BasicWorkflow.class, wfId, 10, input, startOptions);
        // wait for workflow to finish
        final Integer output = client.getSimpleWorkflowResultWithWait(Integer.class, wfId);
        Assertions.assertEquals(input + 2, output);

        // start the same workflow again should fail
        try {
            client.startWorkflow(BasicWorkflow.class, wfId, 10, input, startOptions);
        } catch (ClientSideException e) {
            Assertions.assertEquals(ErrorSubStatus.WORKFLOW_ALREADY_STARTED_SUB_STATUS, e.getErrorSubStatus());
            Assertions.assertEquals(400, e.getStatusCode());
            return;
        }
        Assertions.fail("get results from closed workflow should fail");
    }

    @Test
    public void testEmptyInputWorkflow() throws InterruptedException {
        final Client client = new Client(WorkflowRegistry.registry, ClientOptions.localDefault);
        final String wfId = "empty-input-test-id" + System.currentTimeMillis() / 1000;
        final UnregisteredWorkflowOptions startOptions = ImmutableUnregisteredWorkflowOptions.builder()
                .workflowIdReusePolicy(IDReusePolicy.ALLOW_IF_NO_RUNNING)
                .build();

        //client.StartWorkflow(EmptyInputWorkflow.class, EmptyInputWorkflowState1.StateId, null, wfId, startOptions);
        client.getUnregisteredClient().startWorkflow(EmptyInputWorkflow.CUSTOM_WF_TYPE, EmptyInputWorkflowState1.class.getSimpleName(), wfId, 10, null, startOptions);
        // wait for workflow to finish
        Integer out = client.getSimpleWorkflowResultWithWait(Integer.class, wfId);

        Assertions.assertNull(out);

        try {
            client.getSimpleWorkflowResultWithWait(Integer.class, "a wrong workflowId");
        } catch (ClientSideException e) {
            Assertions.assertEquals(ErrorSubStatus.WORKFLOW_NOT_EXISTS_SUB_STATUS, e.getErrorSubStatus());
            Assertions.assertEquals(400, e.getStatusCode());
            return;
        }
        Assertions.fail("get results from a wrong workflow should fail");
    }

    @Test
    public void testModelInputWorkflow() throws InterruptedException {
        final Client client = new Client(WorkflowRegistry.registry, ClientOptions.localDefault);
        final String wfId = "model-input-test-id" + System.currentTimeMillis() / 1000;
        final Context input = new FakContextImpl();
        client.startWorkflow(ModelInputWorkflow.class, wfId, 10, input);
        client.getSimpleWorkflowResultWithWait(Integer.class, wfId);
        try {
            client.startWorkflow(ModelInputWorkflow.class, wfId, 10, "123");
        } catch (WorkflowDefinitionException e) {
            return;
        }
        Assertions.fail("start workflow with wrong input type should fail");
    }

    @Test
    public void testProceedOnStateStartFailWorkflow() throws InterruptedException {
        final Client client = new Client(WorkflowRegistry.registry, ClientOptions.localDefault);
        final String wfId = "proceed-on-state-start-fail-test-id" + System.currentTimeMillis() / 1000;
        final WorkflowOptions startOptions = ImmutableWorkflowOptions.builder()
                .workflowIdReusePolicy(IDReusePolicy.DISALLOW_REUSE)
                .build();
        final String input = "input";
        client.startWorkflow(ProceedOnStateStartFailWorkflow.class, wfId, 10, input, startOptions);
        // wait for workflow to finish
        final String output = client.getSimpleWorkflowResultWithWait(String.class, wfId);
        Assertions.assertEquals("input_state1_start_state1_decide_state2_start_state2_decide", output);
    }

    @Test
    public void testWorkflowConfigOverride() throws InterruptedException {
        final Client client = new Client(WorkflowRegistry.registry, ClientOptions.localDefault);
        final String wfId = "wf-config-override-test-id" + System.currentTimeMillis() / 1000;
        final WorkflowOptions startOptions = ImmutableWorkflowOptions.builder()
                .workflowIdReusePolicy(IDReusePolicy.DISALLOW_REUSE)
                .workflowConfigOverride(new WorkflowConfig().continueAsNewThreshold(1))
                .build();
        final int input = 0;
        client.startWorkflow(BasicWorkflow.class, wfId, 10, input, startOptions);
        // wait for workflow to finish
        final Integer output = client.getSimpleWorkflowResultWithWait(Integer.class, wfId);
        Assertions.assertEquals(input + 2, output);
    }
}
