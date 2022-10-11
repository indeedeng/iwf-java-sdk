package io.github.cadenceoss.iwf.core;

import com.google.common.base.Preconditions;
import io.github.cadenceoss.iwf.gen.models.*;
import io.github.cadenceoss.iwf.gen.api.ApiClient;
import io.github.cadenceoss.iwf.gen.api.DefaultApi;

import java.util.Map;

public class Client {
    private final Registry registry;
    private final DefaultApi defaultApi;

    private final ClientOptions clientOptions;

    private final ObjectEncoder objectEncoder = new JacksonJsonObjectEncoder();

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
                .stateInput(objectEncoder.encode(input))
                .startStateId(startStateId));
        return workflowStartResponse.getWorkflowRunId();
    }

    public <T> T GetSingleWorkflowStateOutputWithLongWait(
            Class<T> valueClass,
            final String workflowId) {
        WorkflowGetResponse workflowGetResponse = defaultApi.apiV1WorkflowGetWithLongWaitPost(
                new WorkflowGetRequest()
                        .needsResults(true)
                        .workflowId(workflowId)
        );

        String checkErrorMessage = "this workflow should have exactly one state output";
        Preconditions.checkNotNull(workflowGetResponse.getResults(), checkErrorMessage);
        Preconditions.checkArgument(workflowGetResponse.getResults().size() == 1, checkErrorMessage);
        Preconditions.checkNotNull(workflowGetResponse.getResults().get(0).getCompletedStateOutput(), checkErrorMessage);

        //TODO validate encoding type

        final StateCompletionOutput output = workflowGetResponse.getResults().get(0);
        return objectEncoder.decode(output.getCompletedStateOutput(), valueClass);
    }

    public void SignalWorkflow(
            final Class<? extends Workflow> workflowClass,
            final String workflowId,
            final String workflowRunId,
            final String signalName,
            final Object signalValue) {
        final String wfType = workflowClass.getSimpleName();
        if (registry.getSignalNameToSignalTypeMap(wfType) == null) {
            throw new RuntimeException(String.format("Workflow %s doesn't have any registered signal", wfType));
        }

        Map<String, Class<?>> signalNameToTypeMap = registry.getSignalNameToSignalTypeMap(wfType);
        if (!signalNameToTypeMap.containsKey(signalName)) {
            throw new RuntimeException(String.format("Workflow %s doesn't have signal %s", wfType, signalName));
        }
        Class<?> signalType = signalNameToTypeMap.get(signalName);
        if (!signalType.isInstance(signalValue)) {
            throw new RuntimeException(String.format("Signal value is not of type %s", signalType.getName()));
        }

        defaultApi.apiV1WorkflowSignalPost(new WorkflowSignalRequest()
                .workflowId(workflowId)
                .workflowRunId(workflowRunId)
                .signalName(signalName)
                .signalValue(objectEncoder.encode(signalValue)));
    }
}
