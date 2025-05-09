package io.iworkflow.integ;

import io.iworkflow.core.Client;
import io.iworkflow.core.ClientOptions;
import io.iworkflow.core.ImmutableUnregisteredWorkflowOptions;
import io.iworkflow.core.ImmutableWorkflowOptions;
import io.iworkflow.core.UnregisteredWorkflowOptions;
import io.iworkflow.core.WorkflowDefinitionException;
import io.iworkflow.core.WorkflowInfo;
import io.iworkflow.core.WorkflowOptions;
import io.iworkflow.core.WorkflowStateOptions;
import io.iworkflow.core.WorkflowUncompletedException;
import io.iworkflow.core.exceptions.NoRunningWorkflowException;
import io.iworkflow.core.exceptions.WorkflowAlreadyStartedException;
import io.iworkflow.gen.models.Context;
import io.iworkflow.gen.models.ErrorSubStatus;
import io.iworkflow.gen.models.IDReusePolicy;
import io.iworkflow.gen.models.PersistenceLoadingPolicy;
import io.iworkflow.gen.models.PersistenceLoadingType;
import io.iworkflow.gen.models.RetryPolicy;
import io.iworkflow.gen.models.WorkflowConfig;
import io.iworkflow.gen.models.WorkflowStatus;
import io.iworkflow.integ.basic.AbnormalExitWorkflow;
import io.iworkflow.integ.basic.BasicWorkflow;
import io.iworkflow.integ.basic.BasicWorkflowState2;
import io.iworkflow.integ.basic.EmptyInputWorkflow;
import io.iworkflow.integ.basic.EmptyInputWorkflowState1;
import io.iworkflow.integ.basic.FakContextImpl;
import io.iworkflow.integ.basic.MixOfWithWaitUntilAndSkipWaitUntilWorkflow;
import io.iworkflow.integ.basic.ModelInputWorkflow;
import io.iworkflow.integ.basic.ProceedOnStateStartFailWorkflow;
import io.iworkflow.integ.stateoptions.StateOptionsWorkflow;
import io.iworkflow.spring.TestSingletonWorkerService;
import io.iworkflow.spring.controller.WorkflowRegistry;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Collections;
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
        } catch (WorkflowAlreadyStartedException e) {
            Assertions.assertEquals(ErrorSubStatus.WORKFLOW_ALREADY_STARTED_SUB_STATUS, e.getErrorSubStatus());
            Assertions.assertEquals(400, e.getStatusCode());
            return;
        }
        Assertions.fail("get results from closed workflow should fail");
    }

    @Test
    public void testBasicWorkflowAbnormalExitReuse() {
        final Client client = new Client(WorkflowRegistry.registry, ClientOptions.localDefault);
        final String wfId = "basic-abnormal-exit-reuse-test-id" + System.currentTimeMillis() / 1000;
        final WorkflowOptions startOptions = ImmutableWorkflowOptions.builder()
                .workflowIdReusePolicy(IDReusePolicy.ALLOW_IF_PREVIOUS_EXITS_ABNORMALLY)
                .build();

        final Integer input = 0;
        client.startWorkflow(AbnormalExitWorkflow.class, wfId, 10, input, startOptions);
        Assertions.assertThrows(WorkflowUncompletedException.class,
                () -> client.getSimpleWorkflowResultWithWait(Integer.class, wfId));

        // Starting a workflow with the same ID should be allowed since the previous failed abnormally
        client.startWorkflow(BasicWorkflow.class, wfId, 10, input, startOptions);
        // Wait for workflow to finish
        final Integer output = client.getSimpleWorkflowResultWithWait(Integer.class, wfId);
        Assertions.assertEquals(input + 2, output);
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
        } catch (NoRunningWorkflowException e) {
            Assertions.assertEquals(ErrorSubStatus.WORKFLOW_NOT_EXISTS_SUB_STATUS, e.getErrorSubStatus());
            Assertions.assertEquals(400, e.getStatusCode());
            return;
        }
        Assertions.fail("get results from a wrong workflow should fail");
    }

    @Test
    public void testTypeSpecifiedWorkflow() {
        final Client client = new Client(WorkflowRegistry.registry, ClientOptions.localDefault);
        final String wfId = "type-specified-test-id" + System.currentTimeMillis() / 1000;
        final UnregisteredWorkflowOptions startOptions = ImmutableUnregisteredWorkflowOptions.builder()
                .workflowIdReusePolicy(IDReusePolicy.ALLOW_IF_NO_RUNNING)
                .build();

        final EmptyInputWorkflow workflow = new EmptyInputWorkflow();

        client.startWorkflow(workflow.getWorkflowType(), wfId, 0, null, null);
        Integer out = client.getSimpleWorkflowResultWithWait(Integer.class, wfId);
        Assertions.assertNull(out);

        // fail when not passing the customized workflowType when starting a workflow with customized workflowType
        try {
            client.startWorkflow(EmptyInputWorkflow.class, wfId, 0);
        } catch (final IllegalArgumentException e) {
            return;
        }
        Assertions.fail("not passing the customized workflowType when starting a workflow with customized workflowType should fail");
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

    @Test
    public void testGetWorkflowStatusWhenNoExistingWorkflow() {
        final Client client = new Client(WorkflowRegistry.registry, ClientOptions.localDefault);
        final String wfId = "wf-get-workflow-status-test-id" + System.currentTimeMillis() / 1000;

        try {
            client.describeWorkflow(wfId, "");
        } catch (final NoRunningWorkflowException e) {
            Assertions.assertEquals(ErrorSubStatus.WORKFLOW_NOT_EXISTS_SUB_STATUS, e.getErrorSubStatus());
            Assertions.assertEquals(400, e.getStatusCode());
        }
    }

    @Test
    public void testGetWorkflowStatusWhenWorkflowIsRunning() {
        final Client client = new Client(WorkflowRegistry.registry, ClientOptions.localDefault);
        final String wfId = "wf-get-workflow-status-running-test-id" + System.currentTimeMillis() / 1000;

        client.startWorkflow(BasicWorkflow.class, wfId, 10, 0, null);
        final WorkflowInfo workflowInfo = client.describeWorkflow(wfId);
        Assertions.assertEquals(WorkflowStatus.RUNNING, workflowInfo.getWorkflowStatus());
    }

    @Test
    public void testWorkflowWaitForStateCompletionWithWaitForKey() {
        final Client client = new Client(WorkflowRegistry.registry, ClientOptions.localDefault);
        final long startTs = System.currentTimeMillis();
        final String wfId = "wf-wait-for-state-completion-with-wait-for-key-test-id" + startTs / 1000;
        final Integer input = 5;
        final String waitForKey = "testKey";

        client.startWorkflow(
                BasicWorkflow.class, wfId, 10, input,
                WorkflowOptions.extendedBuilder()
                        .waitForCompletionStates(BasicWorkflowState2.class)
                        .getBuilder().build());

        client.waitForStateExecutionCompletion(wfId, BasicWorkflowState2.class, waitForKey);
        client.waitForWorkflowCompletion(wfId);

        final String childWfId = "__IwfSystem_" + wfId + "_" + "BasicWorkflowState2" + "_" + waitForKey;

        final WorkflowInfo workflowInfo = client.describeWorkflow(wfId);
        Assertions.assertEquals(WorkflowStatus.COMPLETED, workflowInfo.getWorkflowStatus());

        final WorkflowInfo childWorkflowInfo = client.describeWorkflow(childWfId);
        Assertions.assertEquals(WorkflowStatus.COMPLETED, childWorkflowInfo.getWorkflowStatus());
    }

    @Test
    public void testMixOfWithWaitUntilAndSkipWaitUntilWorkflow() {
        final Client client = new Client(WorkflowRegistry.registry, ClientOptions.localDefault);
        final long startTs = System.currentTimeMillis();
        final String wfId = "wf-mix-of-with-wait-until-and-skip-wait-until-workflow-test-id" + startTs / 1000;
        final Integer input = 5;

        client.startWorkflow(MixOfWithWaitUntilAndSkipWaitUntilWorkflow.class, wfId, 10, input);
        client.waitForWorkflowCompletion(wfId);
    }

    @Test
    public void deepCopyWorkflowStateOptionsTest() {
        final WorkflowStateOptions origOptions = new WorkflowStateOptions();
        origOptions.setExecuteApiRetryPolicy(new RetryPolicy().maximumAttempts(3));
        origOptions.setExecuteApiTimeoutSeconds(10);
        origOptions.setWaitUntilApiTimeoutSeconds(8);
        origOptions.setSearchAttributesLoadingPolicy(new PersistenceLoadingPolicy().persistenceLoadingType(
                        PersistenceLoadingType.PARTIAL_WITH_EXCLUSIVE_LOCK)
                .partialLoadingKeys(Collections.singletonList(StateOptionsWorkflow.DA_WAIT_UNTIL)));

        final WorkflowStateOptions deepCopyOptions = origOptions.clone();
        Assertions.assertEquals(origOptions, deepCopyOptions);

        // Verify changing a value in one object doesn't update both by reference
        origOptions.getSearchAttributesLoadingPolicy().setPersistenceLoadingType(PersistenceLoadingType.NONE);
        Assertions.assertNotEquals(origOptions, deepCopyOptions);
    }
}
