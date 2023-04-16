package io.iworkflow.integ;

import io.iworkflow.core.Client;
import io.iworkflow.core.ClientOptions;
import io.iworkflow.core.ClientSideException;
import io.iworkflow.core.ImmutableObjectOptions;
import io.iworkflow.core.ImmutableUnregisteredObjectOptions;
import io.iworkflow.core.ObjectDefinitionException;
import io.iworkflow.core.ObjectOptions;
import io.iworkflow.core.UnregisteredObjectOptions;
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
        final ObjectOptions startOptions = ImmutableObjectOptions.builder()
                .objectIdReusePolicy(IDReusePolicy.DISALLOW_REUSE)
                .build();
        final Integer input = 0;
        client.createObject(BasicWorkflow.class, wfId, 10, input, startOptions);
        // wait for workflow to finish
        final Integer output = client.getSingleResultWithWait(Integer.class, wfId);
        Assertions.assertEquals(input + 2, output);

        // start the same workflow again should fail
        try {
            client.createObject(BasicWorkflow.class, wfId, 10, input, startOptions);
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
        final UnregisteredObjectOptions startOptions = ImmutableUnregisteredObjectOptions.builder()
                .objectIdReusePolicy(IDReusePolicy.ALLOW_IF_NO_RUNNING)
                .build();

        //client.StartWorkflow(EmptyInputWorkflow.class, EmptyInputWorkflowState1.StateId, null, wfId, startOptions);
        client.getUnregisteredClient().startObject(EmptyInputWorkflow.CUSTOM_WF_TYPE, EmptyInputWorkflowState1.class.getSimpleName(), wfId, 10, null, startOptions);
        // wait for workflow to finish
        Integer out = client.getSingleResultWithWait(Integer.class, wfId);

        Assertions.assertNull(out);

        try {
            client.getSingleResultWithWait(Integer.class, "a wrong workflowId");
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
        client.createObject(ModelInputWorkflow.class, wfId, 10, input);
        client.getSingleResultWithWait(Integer.class, wfId);
        try {
            client.createObject(ModelInputWorkflow.class, wfId, 10, "123");
        } catch (ObjectDefinitionException e) {
            return;
        }
        Assertions.fail("start workflow with wrong input type should fail");
    }

    @Test
    public void testProceedOnStateStartFailWorkflow() throws InterruptedException {
        final Client client = new Client(WorkflowRegistry.registry, ClientOptions.localDefault);
        final String wfId = "proceed-on-state-start-fail-test-id" + System.currentTimeMillis() / 1000;
        final ObjectOptions startOptions = ImmutableObjectOptions.builder()
                .objectIdReusePolicy(IDReusePolicy.DISALLOW_REUSE)
                .build();
        final String input = "input";
        client.createObject(ProceedOnStateStartFailWorkflow.class, wfId, 10, input, startOptions);
        // wait for workflow to finish
        final String output = client.getSingleResultWithWait(String.class, wfId);
        Assertions.assertEquals("input_state1_start_state1_decide_state2_start_state2_decide", output);
    }

    @Test
    public void testWorkflowConfigOverride() throws InterruptedException {
        final Client client = new Client(WorkflowRegistry.registry, ClientOptions.localDefault);
        final String wfId = "wf-config-override-test-id" + System.currentTimeMillis() / 1000;
        final ObjectOptions startOptions = ImmutableObjectOptions.builder()
                .objectIdReusePolicy(IDReusePolicy.DISALLOW_REUSE)
                .objectConfigOverride(new WorkflowConfig().continueAsNewThreshold(1))
                .build();
        final int input = 0;
        client.createObject(BasicWorkflow.class, wfId, 10, input, startOptions);
        // wait for workflow to finish
        final Integer output = client.getSingleResultWithWait(Integer.class, wfId);
        Assertions.assertEquals(input + 2, output);
    }
}
