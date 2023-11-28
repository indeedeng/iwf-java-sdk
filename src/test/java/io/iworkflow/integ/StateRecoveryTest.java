package io.iworkflow.integ;

import io.iworkflow.core.Client;
import io.iworkflow.core.ClientOptions;
import io.iworkflow.integ.stateapifail.WorkflowStateFailProceedToRecover;
import io.iworkflow.integ.stateapifail.WorkflowStateFailProceedToRecoverNoWaitUntil;
import io.iworkflow.spring.TestSingletonWorkerService;
import io.iworkflow.spring.controller.WorkflowRegistry;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.concurrent.ExecutionException;

public class StateRecoveryTest {

    @BeforeEach
    public void setup() throws ExecutionException, InterruptedException {
        TestSingletonWorkerService.startWorkerIfNotUp();
    }

    @Test
    public void testStateApiFailAndRecoveryWorkflow() throws InterruptedException {
        final Client client = new Client(WorkflowRegistry.registry, ClientOptions.localDefault);
        final long startTs = System.currentTimeMillis();
        final String wfId = "testStateApiFailAndRecoveryWorkflow" + startTs / 1000;
        final Integer input = 5;

        client.startWorkflow(
                WorkflowStateFailProceedToRecover.class, wfId, 10, input);

        Integer output = client.getSimpleWorkflowResultWithWait(Integer.class, wfId);
        Assertions.assertEquals(10, output);
    }

    @Test
    public void testStateApiFailAndRecoveryNoWaitUntilWorkflow() throws InterruptedException {
        final Client client = new Client(WorkflowRegistry.registry, ClientOptions.localDefault);
        final long startTs = System.currentTimeMillis();
        final String wfId = "testStateApiFailAndRecoveryNoWaitUntilWorkflow" + startTs / 1000;
        final Integer input = 5;

        client.startWorkflow(
                WorkflowStateFailProceedToRecoverNoWaitUntil.class, wfId, 10, input);

        Integer output = client.getSimpleWorkflowResultWithWait(Integer.class, wfId);
        Assertions.assertEquals(10, output);
    }

}
