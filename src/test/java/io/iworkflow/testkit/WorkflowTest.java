package io.iworkflow.testkit;

import io.iworkflow.core.Client;
import io.iworkflow.core.ClientOptions;
import io.iworkflow.core.ObjectWorkflow;
import io.iworkflow.spring.TestSingletonWorkerService;
import io.iworkflow.spring.controller.WorkflowRegistry;

import java.util.concurrent.ExecutionException;

public class WorkflowTest {

    protected Client client;

    protected String wfId;


    public void setup() throws ExecutionException, InterruptedException {
        TestSingletonWorkerService.startWorkerIfNotUp();
        this.client = new Client(WorkflowRegistry.registry, ClientOptions.localDefault);
    }

    protected <T extends ObjectWorkflow> T startWorkflowAndReturnStub(
            Class<T> workflowClass,
            String namePrefix,
            int workflowTimeOutSeconds,
            Object input
    ) {
        this.wfId = namePrefix + System.currentTimeMillis() / 1000;
        client.startWorkflow(
                workflowClass, wfId, workflowTimeOutSeconds, input
        );
        return client.newRpcStub(workflowClass, wfId, "");
    }

    protected <T extends ObjectWorkflow> T startWorkflowAndReturnStub(
            Class<T> workflowClass,
            String namePrefix,
            int workflowTimeOutSeconds
    ) {
        this.wfId = namePrefix + System.currentTimeMillis() / 1000;
        client.startWorkflow(
                workflowClass, wfId, workflowTimeOutSeconds
        );
        return client.newRpcStub(workflowClass, wfId, "");
    }

}
