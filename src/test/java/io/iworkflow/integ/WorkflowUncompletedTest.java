package io.iworkflow.integ;

import io.iworkflow.core.Client;
import io.iworkflow.core.ClientOptions;
import io.iworkflow.core.WorkflowUncompletedException;
import io.iworkflow.gen.models.WorkflowErrorType;
import io.iworkflow.gen.models.WorkflowStatus;
import io.iworkflow.integ.forcefail.ForceFailWorkflow;
import io.iworkflow.spring.TestSingletonWorkerService;
import io.iworkflow.spring.controller.WorkflowRegistry;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.concurrent.ExecutionException;

public class WorkflowUncompletedTest {

    @BeforeEach
    public void setup() throws ExecutionException, InterruptedException {
        TestSingletonWorkerService.startWorkerIfNotUp();
    }

    @Test
    public void testForceFailWorkflow() throws InterruptedException {
        final Client client = new Client(WorkflowRegistry.registry, ClientOptions.localDefault);
        final long startTs = System.currentTimeMillis();
        final String wfId = "testForceFailWorkflow" + startTs / 1000;
        final Integer input = 5;

        final String runId = client.startWorkflow(
                ForceFailWorkflow.class, wfId, 10, input);

        try {
            client.getSimpleWorkflowResultWithWait(Integer.class, wfId);
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
}
