package io.github.cadenceoss.iwf.integ;

import io.github.cadenceoss.iwf.core.Client;
import io.github.cadenceoss.iwf.core.ClientOptions;
import io.github.cadenceoss.iwf.core.WorkflowStartOptions;
import io.github.cadenceoss.iwf.integ.basic.BasicWorkflow;
import io.github.cadenceoss.iwf.integ.basic.BasicWorkflowS1;
import io.github.cadenceoss.iwf.integ.query.BasicQueryWorkflow;
import io.github.cadenceoss.iwf.integ.query.BasicQueryWorkflowState1;
import io.github.cadenceoss.iwf.integ.signal.BasicSignalWorkflow;
import io.github.cadenceoss.iwf.integ.signal.BasicSignalWorkflowState1;
import io.github.cadenceoss.iwf.integ.timer.BasicTimerWorkflow;
import io.github.cadenceoss.iwf.integ.timer.BasicTimerWorkflowState1;
import io.github.cadenceoss.iwf.spring.TestSingletonWorkerService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.Map;

public class IntegrationTest {

    @BeforeAll
    public static void setup() {
        TestSingletonWorkerService.startWorkerIfNotUp();
    }

    @Test
    public void testBasicWorkflow() {
        final Client client = new Client(WorkflowRegistry.registry, ClientOptions.localDockerDefault);
        final String wfId = "basic-test-id" + System.currentTimeMillis() / 1000;
        final WorkflowStartOptions startOptions = WorkflowStartOptions.minimum(10);
        final Integer input = 0;
        client.StartWorkflow(BasicWorkflow.class, BasicWorkflowS1.StateId, input, wfId, startOptions);
        // wait for workflow to finish
        final Integer output = client.GetSimpleWorkflowResultWithWait(Integer.class, wfId);
        Assertions.assertEquals(input + 2, output);
    }

    @Test
    public void testBasicSignalWorkflow() {
        final Client client = new Client(WorkflowRegistry.registry, ClientOptions.localDockerDefault);
        final String wfId = "basic-signal-test-id" + System.currentTimeMillis() / 1000;
        final WorkflowStartOptions startOptions = WorkflowStartOptions.minimum(10);
        final Integer input = 1;
        final String runId = client.StartWorkflow(
                BasicSignalWorkflow.class, BasicSignalWorkflowState1.STATE_ID, input, wfId, startOptions);
        client.SignalWorkflow(
                BasicSignalWorkflow.class, wfId, runId, BasicSignalWorkflowState1.SIGNAL_CHANNEL_NAME, 2);
        final Integer output = client.GetSimpleWorkflowResultWithWait(Integer.class, wfId);
        Assertions.assertEquals(3, output);
    }

    @Test
    public void testBasicQueryWorkflow() {
        final Client client = new Client(WorkflowRegistry.registry, ClientOptions.localDockerDefault);
        final String wfId = "basic-query-test-id" + System.currentTimeMillis() / 1000;
        final WorkflowStartOptions startOptions = WorkflowStartOptions.minimum(10);
        final String runId = client.StartWorkflow(
                BasicQueryWorkflow.class, BasicQueryWorkflowState1.STATE_ID, "start", wfId, startOptions);
        final String output = client.GetSimpleWorkflowResultWithWait(String.class, wfId);
        Map<String, Object> map =
                client.GetWorkflowQueryAttributes(BasicQueryWorkflow.class, wfId, runId, Collections.singletonList(BasicQueryWorkflow.ATTRIBUTE_KEY));
        Assertions.assertEquals(
                "query-start-query-decide", map.get(BasicQueryWorkflow.ATTRIBUTE_KEY));
        Map<String, Object> allQueryAttributes =
                client.GetAllQueryAttributes(BasicQueryWorkflow.class, wfId, runId);
        Assertions.assertEquals(
                "query-start-query-decide", allQueryAttributes.get(BasicQueryWorkflow.ATTRIBUTE_KEY));
        Assertions.assertEquals(
                1, allQueryAttributes.size());
    }

    @Test
    public void testBasicTimerWorkflow() {
        final Client client = new Client(WorkflowRegistry.registry, ClientOptions.localDockerDefault);
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
