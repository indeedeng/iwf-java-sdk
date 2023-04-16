package io.iworkflow.integ;

import io.iworkflow.core.Client;
import io.iworkflow.core.ClientOptions;
import io.iworkflow.core.ImmutableStopWorkflowOptions;
import io.iworkflow.core.WorkflowUncompletedException;
import io.iworkflow.gen.models.WorkflowErrorType;
import io.iworkflow.gen.models.WorkflowStatus;
import io.iworkflow.gen.models.WorkflowStopType;
import io.iworkflow.integ.forcefail.ForceFailWorkflow;
import io.iworkflow.integ.signal.BasicSignalWorkflow;
import io.iworkflow.integ.stateapifail.StateApiFailWorkflow;
import io.iworkflow.integ.stateapitimeout.StateApiTimeoutFailWorkflow;
import io.iworkflow.spring.TestSingletonWorkerService;
import io.iworkflow.spring.controller.WorkflowRegistry;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.concurrent.ExecutionException;

import static io.iworkflow.gen.models.WorkflowErrorType.CLIENT_API_FAILING_WORKFLOW_ERROR_TYPE;

public class WorkflowUncompletedTest {

    @BeforeEach
    public void setup() throws ExecutionException, InterruptedException {
        TestSingletonWorkerService.startWorkerIfNotUp();
    }

    @Test
    public void testWorkflowTimeout() throws InterruptedException {
        final Client client = new Client(WorkflowRegistry.registry, ClientOptions.localDefault);
        final String wfId = "testWorkflowTimeout" + System.currentTimeMillis() / 1000;
        final Integer input = 1;
        final String runId = client.createObject(
                BasicSignalWorkflow.class, wfId, 1, input);

        try {
            client.getSingleResultWithWait(Integer.class, wfId);
        } catch (WorkflowUncompletedException e) {
            Assertions.assertEquals(runId, e.getRunId());
            Assertions.assertEquals(WorkflowStatus.TIMEOUT, e.getClosedStatus());
            Assertions.assertNull(e.getErrorSubType());
            Assertions.assertNull(e.getErrorMessage());
            Assertions.assertEquals(0, e.getStateResultsSize());
            return;
        }
        Assertions.fail("no exception caught");
    }

    @Test
    public void testWorkflowCanceled() throws InterruptedException {
        final Client client = new Client(WorkflowRegistry.registry, ClientOptions.localDefault);
        final String wfId = "testWorkflowTimeout" + System.currentTimeMillis() / 1000;
        final Integer input = 1;
        final String runId = client.createObject(
                BasicSignalWorkflow.class, wfId, 10, input);

        client.closeObjectExecution(wfId, "");

        try {
            client.getSingleResultWithWait(Integer.class, wfId);
        } catch (WorkflowUncompletedException e) {
            Assertions.assertEquals(runId, e.getRunId());
            Assertions.assertEquals(WorkflowStatus.CANCELED, e.getClosedStatus());
            Assertions.assertNull(e.getErrorSubType());
            Assertions.assertNull(e.getErrorMessage());
            Assertions.assertEquals(0, e.getStateResultsSize());
            return;
        }
        Assertions.fail("no exception caught");
    }

    @Test
    public void testWorkflowTerminated() throws InterruptedException {
        final Client client = new Client(WorkflowRegistry.registry, ClientOptions.localDefault);
        final String wfId = "testWorkflowTerminated" + System.currentTimeMillis() / 1000;
        final Integer input = 1;
        final String runId = client.createObject(
                BasicSignalWorkflow.class, wfId, 10, input);

        client.closeObjectExecution(wfId, "", ImmutableStopWorkflowOptions.builder().workflowStopType(WorkflowStopType.TERMINATE).build());

        try {
            client.getSingleResultWithWait(Integer.class, wfId);
        } catch (WorkflowUncompletedException e) {
            Assertions.assertEquals(runId, e.getRunId());
            Assertions.assertEquals(WorkflowStatus.TERMINATED, e.getClosedStatus());
            Assertions.assertNull(e.getErrorSubType());
            Assertions.assertNull(e.getErrorMessage());
            Assertions.assertEquals(0, e.getStateResultsSize());
            return;
        }
        Assertions.fail("no exception caught");
    }

    @Test
    public void testWorkflowFailByAPI() throws InterruptedException {
        final Client client = new Client(WorkflowRegistry.registry, ClientOptions.localDefault);
        final String wfId = "testWorkflowTerminated" + System.currentTimeMillis() / 1000;
        final Integer input = 1;
        final String runId = client.createObject(
                BasicSignalWorkflow.class, wfId, 10, input);

        client.closeObjectExecution(wfId, "", ImmutableStopWorkflowOptions.builder().workflowStopType(WorkflowStopType.FAIL).reason("fail by API").build());

        try {
            client.getSingleResultWithWait(Integer.class, wfId);
        } catch (WorkflowUncompletedException e) {
            Assertions.assertEquals(runId, e.getRunId());
            Assertions.assertEquals(WorkflowStatus.FAILED, e.getClosedStatus());
            Assertions.assertEquals(CLIENT_API_FAILING_WORKFLOW_ERROR_TYPE, e.getErrorSubType());
            Assertions.assertEquals("fail by API", e.getErrorMessage());
            Assertions.assertEquals(0, e.getStateResultsSize());
            return;
        }
        Assertions.fail("no exception caught");
    }

    @Test
    public void testForceFailWorkflow() throws InterruptedException {
        final Client client = new Client(WorkflowRegistry.registry, ClientOptions.localDefault);
        final long startTs = System.currentTimeMillis();
        final String wfId = "testForceFailWorkflow" + startTs / 1000;
        final Integer input = 5;

        final String runId = client.createObject(
                ForceFailWorkflow.class, wfId, 10, input);

        try {
            client.getSingleResultWithWait(Integer.class, wfId);
        } catch (WorkflowUncompletedException e) {
            Assertions.assertEquals(runId, e.getRunId());
            Assertions.assertEquals(WorkflowStatus.FAILED, e.getClosedStatus());
            Assertions.assertEquals(WorkflowErrorType.STATE_DECISION_FAILING_WORKFLOW_ERROR_TYPE, e.getErrorSubType());
            Assertions.assertNull(e.getErrorMessage());
            Assertions.assertEquals(1, e.getStateResultsSize());
            String out = e.getStateResult(0, String.class);
            Assertions.assertEquals("a failing message", out);
            return;
        }
        Assertions.fail("no exception caught");
    }

    @Test
    public void testStateApiFailWorkflow() throws InterruptedException {
        final Client client = new Client(WorkflowRegistry.registry, ClientOptions.localDefault);
        final long startTs = System.currentTimeMillis();
        final String wfId = "testStateApiFailWorkflow" + startTs / 1000;
        final Integer input = 5;

        final String runId = client.createObject(
                StateApiFailWorkflow.class, wfId, 10, input);

        try {
            client.getSingleResultWithWait(Integer.class, wfId);
        } catch (WorkflowUncompletedException e) {
            Assertions.assertEquals(runId, e.getRunId());
            Assertions.assertEquals(WorkflowStatus.FAILED, e.getClosedStatus());
            Assertions.assertEquals(WorkflowErrorType.STATE_API_FAIL_MAX_OUT_RETRY_ERROR_TYPE, e.getErrorSubType());
            Assertions.assertTrue(e.getErrorMessage().contains("/api/v1/workflowState/decide"));
            Assertions.assertEquals(0, e.getStateResultsSize());
            return;
        }
        Assertions.fail("no exception caught");
    }

    @Test
    public void testStateApiTimeoutWorkflow() throws InterruptedException {
        final Client client = new Client(WorkflowRegistry.registry, ClientOptions.localDefault);
        final long startTs = System.currentTimeMillis();
        final String wfId = "testStateApiTimeoutWorkflow" + startTs / 1000;
        final Integer input = 5;

        final String runId = client.createObject(
                StateApiTimeoutFailWorkflow.class, wfId, 10, input);

        try {
            client.getSingleResultWithWait(Integer.class, wfId);
        } catch (WorkflowUncompletedException e) {
            Assertions.assertEquals(runId, e.getRunId());
            Assertions.assertEquals(WorkflowStatus.FAILED, e.getClosedStatus());
            Assertions.assertEquals(WorkflowErrorType.STATE_API_FAIL_MAX_OUT_RETRY_ERROR_TYPE, e.getErrorSubType());
            Assertions.assertTrue(
                    e.getErrorMessage().contains("activity StartToClose timeout"),
                    e.getErrorMessage()
            );
            Assertions.assertEquals(0, e.getStateResultsSize());
            return;
        }
        Assertions.fail("no exception caught");
    }
}
