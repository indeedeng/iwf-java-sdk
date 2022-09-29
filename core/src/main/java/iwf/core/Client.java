package iwf.core;

import iwf.gen.api.ApiClient;
import iwf.gen.api.DefaultApi;
import iwf.gen.models.WorkflowStartRequest;
import iwf.gen.models.WorkflowStartResponse;

public class Client {
    private final Registry registry;
    private final DefaultApi defaultApi;

    public Client(final Registry registry, final String iwfServerUrl) {
        this.registry = registry;
        this.defaultApi = new ApiClient()
                .setBasePath(iwfServerUrl)
                .buildClient(DefaultApi.class);
    }

    public String StartWorkflow(
            final Workflow workflow,
            final String startStateId,
            final Object input,
            final String workflowId,
            final WorkflowStartOptions options){
        // call iwf server to start the workflow using ApiClient from generated code
        WorkflowStartResponse workflowStartResponse = defaultApi.apiV1WorkflowStartPost(new WorkflowStartRequest()
                .workflowId(workflowId)
                .startStateId(startStateId));
        return workflowStartResponse.getWorkflowRunId();
    }
}
