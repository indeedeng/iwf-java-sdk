package io.github.cadenceoss.iwf.integ;

import io.github.cadenceoss.iwf.core.Client;
import io.github.cadenceoss.iwf.core.ClientOptions;
import io.github.cadenceoss.iwf.core.WorkflowStartOptions;
import io.github.cadenceoss.iwf.integ.timer.BasicTimerWorkflow;
import io.github.cadenceoss.iwf.integ.timer.BasicTimerWorkflowState1;
import io.github.cadenceoss.iwf.spring.TestSingletonWorkerService;
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
        final WorkflowStartOptions startOptions = WorkflowStartOptions.minimum(10);
        final Integer input = 5;

        client.StartWorkflow(
                BasicTimerWorkflow.class, BasicTimerWorkflowState1.STATE_ID, input, wfId, startOptions);

        client.GetSimpleWorkflowResultWithWait(Integer.class, wfId);
        final long elapsed = System.currentTimeMillis() - startTs;
        Assertions.assertTrue(elapsed >= 4000 && elapsed <= 7000, String.format("actual duration: %d", elapsed));
    }
}
