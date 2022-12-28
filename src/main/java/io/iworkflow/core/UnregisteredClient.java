package io.iworkflow.core;

import com.google.common.base.Preconditions;
import io.iworkflow.core.validator.CronScheduleValidator;
import io.iworkflow.gen.api.ApiClient;
import io.iworkflow.gen.api.DefaultApi;
import io.iworkflow.gen.models.SearchAttributeKeyAndType;
import io.iworkflow.gen.models.StateCompletionOutput;
import io.iworkflow.gen.models.WorkflowGetDataObjectsRequest;
import io.iworkflow.gen.models.WorkflowGetDataObjectsResponse;
import io.iworkflow.gen.models.WorkflowGetRequest;
import io.iworkflow.gen.models.WorkflowGetResponse;
import io.iworkflow.gen.models.WorkflowGetSearchAttributesRequest;
import io.iworkflow.gen.models.WorkflowGetSearchAttributesResponse;
import io.iworkflow.gen.models.WorkflowResetRequest;
import io.iworkflow.gen.models.WorkflowResetResponse;
import io.iworkflow.gen.models.WorkflowSearchRequest;
import io.iworkflow.gen.models.WorkflowSearchResponse;
import io.iworkflow.gen.models.WorkflowSignalRequest;
import io.iworkflow.gen.models.WorkflowStartRequest;
import io.iworkflow.gen.models.WorkflowStartResponse;
import io.iworkflow.gen.models.WorkflowStopRequest;

import java.util.List;

/**
 * UntypedClient will let you invoke the APIs to iWF server without much type validation checks(workflow type, signalChannelName, etc).
 * It's useful for calling Client APIs without workflow registry(which may require to have all the workflow dependencies)
 */
public class UnregisteredClient {
    private final DefaultApi defaultApi;

    private final ClientOptions clientOptions;

    public UnregisteredClient(final ClientOptions clientOptions) {
        this.clientOptions = clientOptions;
        this.defaultApi = new ApiClient()
                .setBasePath(clientOptions.getServerUrl())
                .buildClient(DefaultApi.class);
    }

    public String startWorkflow(
            final String workflowType,
            final String startStateId,
            final Object input,
            final String workflowId,
            final WorkflowOptions options) {

        final io.iworkflow.gen.models.WorkflowStartOptions startOptions = new io.iworkflow.gen.models.WorkflowStartOptions();
        if (options.getCronSchedule().isPresent()) {
            startOptions.cronSchedule(CronScheduleValidator.validate(options.getCronSchedule()));
        }
        if (options.getWorkflowIdReusePolicy().isPresent()) {
            startOptions.workflowIDReusePolicy(options.getWorkflowIdReusePolicy().get());
        }
        if (options.getWorkflowRetryPolicy().isPresent()) {
            startOptions.retryPolicy(options.getWorkflowRetryPolicy().get());
        }

        final WorkflowStartRequest request = new WorkflowStartRequest()
                .workflowId(workflowId)
                .iwfWorkerUrl(clientOptions.getWorkerUrl())
                .iwfWorkflowType(workflowType)
                .workflowTimeoutSeconds(options.getWorkflowTimeoutSeconds())
                .stateInput(clientOptions.getObjectEncoder().encode(input))
                .startStateId(startStateId)
                .workflowStartOptions(startOptions);

        if (options.getStartStateOptions().isPresent()) {
            request.stateOptions(options.getStartStateOptions().get());
        }

        WorkflowStartResponse workflowStartResponse = defaultApi.apiV1WorkflowStartPost(request);

        return workflowStartResponse.getWorkflowRunId();
    }

    /**
     * For most cases, a workflow only has one result(one completion state)
     * Use this API to retrieve the output of the state
     *
     * @param valueClass    the type class of the output
     * @param workflowId    the workflowId
     * @param workflowRunId optional runId, can be empty string
     * @param <T>           type of the output
     * @return the result
     */
    public <T> T getSimpleWorkflowResultWithWait(
            Class<T> valueClass,
            final String workflowId,
            final String workflowRunId) {
        WorkflowGetResponse workflowGetResponse = defaultApi.apiV1WorkflowGetWithWaitPost(
                new WorkflowGetRequest()
                        .needsResults(true)
                        .workflowId(workflowId)
                        .workflowRunId(workflowRunId)
        );

        if (workflowGetResponse.getResults() == null || workflowGetResponse.getResults().size() == 0) {
            return null;
        }

        String checkErrorMessage = "this workflow should have one or zero state output for using this API";
        Preconditions.checkNotNull(workflowGetResponse.getResults(), checkErrorMessage);
        Preconditions.checkArgument(workflowGetResponse.getResults().size() == 1, checkErrorMessage);
        Preconditions.checkNotNull(workflowGetResponse.getResults().get(0).getCompletedStateOutput(), checkErrorMessage);

        final StateCompletionOutput output = workflowGetResponse.getResults().get(0);
        return clientOptions.getObjectEncoder().decode(output.getCompletedStateOutput(), valueClass);
    }

    public <T> T getSimpleWorkflowResultWithWait(
            Class<T> valueClass,
            final String workflowId) {
        return getSimpleWorkflowResultWithWait(valueClass, workflowId, "");
    }

    /**
     * In some cases, a workflow may have more than one completion states
     *
     * @param workflowId    workflowId
     * @param workflowRunId optional runId, can be empty string
     * @return a list of the state output for completion states. User code will figure how to use ObjectEncoder to decode the output
     */
    public List<StateCompletionOutput> getComplexWorkflowResultWithWait(
            final String workflowId, final String workflowRunId) {
        WorkflowGetResponse workflowGetResponse = defaultApi.apiV1WorkflowGetWithWaitPost(
                new WorkflowGetRequest()
                        .needsResults(true)
                        .workflowId(workflowId)
                        .workflowRunId(workflowRunId)
        );

        return workflowGetResponse.getResults();
    }

    public void signalWorkflow(
            final String workflowId,
            final String workflowRunId,
            final String signalChannelName,
            final Object signalValue) {
        defaultApi.apiV1WorkflowSignalPost(new WorkflowSignalRequest()
                .workflowId(workflowId)
                .workflowRunId(workflowRunId)
                .signalChannelName(signalChannelName)
                .signalValue(clientOptions.getObjectEncoder().encode(signalValue)));
    }

    /**
     * @param workflowId                  workflowId
     * @param workflowRunId               workflowRunId
     * @param resetWorkflowTypeAndOptions the combination parameter for reset
     * @return the new runId after reset
     */
    public String resetWorkflow(
            final String workflowId,
            final String workflowRunId,
            final ResetWorkflowTypeAndOptions resetWorkflowTypeAndOptions
    ) {

        final WorkflowResetRequest request = new WorkflowResetRequest()
                .workflowId(workflowId)
                .workflowRunId(workflowRunId)
                .resetType(resetWorkflowTypeAndOptions.getResetType())
                .reason(resetWorkflowTypeAndOptions.getReason());
        if (resetWorkflowTypeAndOptions.getHistoryEventId().isPresent()) {
            request.historyEventId(resetWorkflowTypeAndOptions.getHistoryEventId().get());
        }
        if (resetWorkflowTypeAndOptions.getHistoryEventTime().isPresent()) {
            request.historyEventTime(resetWorkflowTypeAndOptions.getHistoryEventTime().get());
        }
        if (resetWorkflowTypeAndOptions.getSkipSignalReapply().isPresent()) {
            request.skipSignalReapply(resetWorkflowTypeAndOptions.getSkipSignalReapply().get());
        }
        if (resetWorkflowTypeAndOptions.getStateId().isPresent()) {
            request.stateId(resetWorkflowTypeAndOptions.getStateId().get());
        }
        if (resetWorkflowTypeAndOptions.getStateExecutionId().isPresent()) {
            request.stateExecutionId(resetWorkflowTypeAndOptions.getStateExecutionId().get());
        }

        final WorkflowResetResponse resp = defaultApi.apiV1WorkflowResetPost(request);
        return resp.getWorkflowRunId();
    }

    /**
     * Stop a workflow, this is essentially terminate the workflow gracefully
     *
     * @param workflowId    required
     * @param workflowRunId optional
     */
    public void StopWorkflow(
            final String workflowId,
            final String workflowRunId) {
        defaultApi.apiV1WorkflowStopPost(new WorkflowStopRequest()
                .workflowId(workflowId)
                .workflowRunId(workflowRunId));
    }

    /**
     * @param workflowId workflowId
     * @param workflowRunId workflowRunId
     * @param attributeKeys, return all attributes if this is empty or null
     * @return the response
     */
    public WorkflowGetDataObjectsResponse getAnyWorkflowDataObjects(
            final String workflowId,
            final String workflowRunId,
            List<String> attributeKeys) {

        return defaultApi.apiV1WorkflowDataobjectsGetPost(
                new WorkflowGetDataObjectsRequest()
                        .workflowId(workflowId)
                        .workflowRunId(workflowRunId)
                        .keys(attributeKeys)
        );
    }

    public WorkflowSearchResponse searchWorkflow(final String query, final int pageSize) {
        return defaultApi.apiV1WorkflowSearchPost(
                new WorkflowSearchRequest()
                        .query(query)
                        .pageSize(pageSize)
        );
    }

    public WorkflowSearchResponse searchWorkflow(final WorkflowSearchRequest request) {
        return defaultApi.apiV1WorkflowSearchPost(request);
    }

    public WorkflowGetSearchAttributesResponse getAnyWorkflowSearchAttributes(
            final String workflowId,
            final String workflowRunId,
            List<SearchAttributeKeyAndType> attributeKeys) {
        return defaultApi.apiV1WorkflowSearchattributesGetPost(
                new WorkflowGetSearchAttributesRequest()
                        .workflowId(workflowId)
                        .workflowRunId(workflowRunId)
                        .keys(attributeKeys)
        );
    }
}
