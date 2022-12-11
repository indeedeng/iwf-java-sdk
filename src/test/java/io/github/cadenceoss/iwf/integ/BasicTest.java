package io.github.cadenceoss.iwf.integ;

import io.github.cadenceoss.iwf.core.Client;
import io.github.cadenceoss.iwf.core.ClientOptions;
import io.github.cadenceoss.iwf.core.ImmutableWorkflowStartOptions;
import io.github.cadenceoss.iwf.core.WorkflowStartOptions;
import io.github.cadenceoss.iwf.core.options.WorkflowIdReusePolicy;
import io.github.cadenceoss.iwf.integ.basic.BasicWorkflow;
import io.github.cadenceoss.iwf.integ.basic.BasicWorkflowState1;
import io.github.cadenceoss.iwf.integ.basic.EmptyInputWorkflow;
import io.github.cadenceoss.iwf.integ.basic.EmptyInputWorkflowState1;
import io.github.cadenceoss.iwf.spring.TestSingletonWorkerService;
import io.github.cadenceoss.iwf.spring.controller.WorkflowRegistry;
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
        final WorkflowStartOptions startOptions = ImmutableWorkflowStartOptions.builder()
                .workflowTimeoutSeconds(10)
                .workflowIdReusePolicy(Optional.of(WorkflowIdReusePolicy.ALLOW_DUPLICATE))
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
        final WorkflowStartOptions startOptions = ImmutableWorkflowStartOptions.builder()
                .workflowTimeoutSeconds(10)
                .workflowIdReusePolicy(Optional.of(WorkflowIdReusePolicy.ALLOW_DUPLICATE))
                .build();
        
        //client.StartWorkflow(EmptyInputWorkflow.class, EmptyInputWorkflowState1.StateId, null, wfId, startOptions);
        client.getUntypedClient().startWorkflow(EmptyInputWorkflow.CUSTOM_WF_TYPE, EmptyInputWorkflowState1.StateId, null, wfId, startOptions);
        // wait for workflow to finish
        client.getSimpleWorkflowResultWithWait(Integer.class, wfId);
    }
}
