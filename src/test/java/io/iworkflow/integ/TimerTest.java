package io.iworkflow.integ;

import io.iworkflow.core.Client;
import io.iworkflow.core.ClientOptions;
import io.iworkflow.core.ImmutableWorkflowOptions;
import io.iworkflow.core.WorkflowOptionBuilderExtension;
import io.iworkflow.core.WorkflowOptions;
import io.iworkflow.integ.timer.BasicTimerWorkflow;
import io.iworkflow.integ.timer.BasicTimerWorkflowState1;
import io.iworkflow.spring.TestSingletonWorkerService;
import io.iworkflow.spring.controller.WorkflowRegistry;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.concurrent.ExecutionException;

public class TimerTest {

    @BeforeEach
    public void setup() throws ExecutionException, InterruptedException {
        TestSingletonWorkerService.startWorkerIfNotUp();
    }

    @Test
    public void testBasicTimerWorkflow() throws InterruptedException {
        final Client client = new Client(WorkflowRegistry.registry, ClientOptions.localDefault);
        final long startTs = System.currentTimeMillis();
        final String wfId = "basic-timer-test-id" + startTs / 1000;
        final Integer input = 5;

        client.startWorkflow(
                BasicTimerWorkflow.class, wfId, 10, input,
                WorkflowOptions.extendedBuilder()
                        .WaitForCompletionStates(BasicTimerWorkflowState1.class)
                        .getBuilder().build());

        client.waitForStateExecutionCompletion(wfId, BasicTimerWorkflowState1.class);
        client.waitForWorkflowCompletion(wfId);
        final long elapsed = System.currentTimeMillis() - startTs;
        Assertions.assertTrue(elapsed >= 4000 && elapsed <= 7000, String.format("actual duration: %d", elapsed));
    }
}
