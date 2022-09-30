package iwf.core;

import iwf.gen.api.ApiClient;
import iwf.gen.api.DefaultApi;
import iwf.gen.models.WorkflowStartRequest;
import iwf.gen.models.WorkflowStartResponse;

public class Client {
    private final Registry registry;
    private final DefaultApi defaultApi;

    private final ClientOptions clientOptions;

    public Client(final Registry registry, final ClientOptions clientOptions) {
        this.clientOptions = clientOptions;
        this.registry = registry;
        this.defaultApi = new ApiClient()
                .setBasePath(clientOptions.getServerUrl())
                .buildClient(DefaultApi.class);
    }

    public String StartWorkflow(
            final Class<? extends Workflow> workflowClass,
            final String startStateId,
            final String workflowId,
            final WorkflowStartOptions options) {
        return StartWorkflow(workflowClass, startStateId, null, workflowId, options);
    }
    
    public String StartWorkflow(
            final Class<? extends Workflow> workflowClass,
            final String startStateId,
            final Object input,
            final String workflowId,
            final WorkflowStartOptions options) {
        final String wfType = workflowClass.getSimpleName();
        final StateDef stateDef = registry.getWorkflowState(wfType, startStateId);
        if (stateDef == null || !stateDef.getCanStartWorkflow()) {
            throw new RuntimeException("invalid start stateId " + startStateId);
        }
        WorkflowStartResponse workflowStartResponse = defaultApi.apiV1WorkflowStartPost(new WorkflowStartRequest()
                .workflowId(workflowId)
                .iwfWorkerUrl(clientOptions.getWorkerUrl())
                .iwfWorkflowType(wfType)
                .workflowTimeoutSeconds(options.getWorkflowTimeoutSeconds())
                .startStateId(startStateId));
        return workflowStartResponse.getWorkflowRunId();
    }
}
