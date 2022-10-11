package io.github.cadenceoss.iwf.core;

import com.google.common.base.Preconditions;
import io.github.cadenceoss.iwf.gen.models.*;
import io.github.cadenceoss.iwf.gen.api.ApiClient;
import io.github.cadenceoss.iwf.gen.api.DefaultApi;

import java.util.List;
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

    /**
     * For most cases, a workflow only has one result(one completion state)
     * Use this API to retrieve the output of the state
     * @param valueClass the type class of the output
     * @param workflowId the workflowId
     * @param workflowRunId optional runId
     * @return
     * @param <T> type of the output
     */
    public <T> T GetSimpleWorkflowResultWithLongWait(
            Class<T> valueClass,
            final String workflowId,
            final String workflowRunId) {
        WorkflowGetResponse workflowGetResponse = defaultApi.apiV1WorkflowGetWithLongWaitPost(
                new WorkflowGetRequest()
                        .needsResults(true)
                        .workflowId(workflowId)
                        .workflowRunId(workflowRunId)
        );

        String checkErrorMessage = "this workflow should have exactly one state output";
        Preconditions.checkNotNull(workflowGetResponse.getResults(), checkErrorMessage);
        Preconditions.checkArgument(workflowGetResponse.getResults().size() == 1, checkErrorMessage);
        Preconditions.checkNotNull(workflowGetResponse.getResults().get(0).getCompletedStateOutput(), checkErrorMessage);

        //TODO validate encoding type

        final StateCompletionOutput output = workflowGetResponse.getResults().get(0);
        return objectEncoder.decode(output.getCompletedStateOutput(), valueClass);
    }

    public <T> T GetSimpleWorkflowResultWithLongWait(
            Class<T> valueClass,
            final String workflowId) {
        return GetSimpleWorkflowResultWithLongWait(valueClass, workflowId, "");
    }

    /**
     * In some cases, a workflow may have more than one completion states
     * @param workflowId
     * @param workflowRunId
     * @return a list of the state output for completion states. User code will figure how to use ObjectEncoder to decode the output
     */
    public List<StateCompletionOutput> GetComplexWorkflowResultWithLongWait(
            final String workflowId, final String workflowRunId) {
        WorkflowGetResponse workflowGetResponse = defaultApi.apiV1WorkflowGetWithLongWaitPost(
                new WorkflowGetRequest()
                        .needsResults(true)
                        .workflowId(workflowId)
                        .workflowRunId(workflowRunId)
        );

        return workflowGetResponse.getResults();
    }

    public List<StateCompletionOutput> GetComplexWorkflowResultWithLongWait(final String workflowId) {
        return GetComplexWorkflowResultWithLongWait(workflowId, "");
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

    /**
     * @param workflowId required
     * @param workflowRunId optional, default to current runId
     * @param resetType rquired
     * @param historyEventId required for resetType of HISTORY_EVENT_ID. The eventID of any event after DecisionTaskStarted you want to reset to (this event is exclusive in a new run. The new run history will fork and continue from the previous eventID of this). It can be DecisionTaskCompleted, DecisionTaskFailed or others
     * @param reason reason to do the reset for tracking purpose
     * @param resetBadBinaryChecksum required for resetType of BAD_BINARY. Binary checksum for resetType of BadBinary
     * @param decisionOffset based on the reset point calculated by resetType, this offset will move/offset the point by decision. Currently only negative number is supported, and only works with LastDecisionCompleted
     * @param earliestTime required for resetType of DECISION_COMPLETED_TIME. EarliestTime of decision start time, required for resetType of DecisionCompletedTime.Supported formats are '2006-01-02T15:04:05+07:00', raw UnixNano and time range (N<duration>), where 0 < N < 1000000 and duration (full-notation/short-notation) can be second/s, minute/m, hour/h, day/d, week/w, month/M or year/y. For example, '15minute' or '15m' implies last 15 minutes, meaning that workflow will be reset to the first decision that completed in last 15 minutes
     * @param skipSignalReapply
     * @return
     */
    public String ResetWorkflow(
            final String workflowId,
            final String workflowRunId,
            final WorkflowResetRequest.ResetTypeEnum resetType,
            final int historyEventId,
            final String reason,
            final String resetBadBinaryChecksum,
            final int decisionOffset,
            final String earliestTime,
            final boolean skipSignalReapply
            ){

        final WorkflowResetResponse resp = defaultApi.apiV1WorkflowResetPost(new WorkflowResetRequest()
                .workflowId(workflowId)
                .workflowRunId(workflowRunId)
                .resetType(resetType)
                .historyEventId(historyEventId)
                .reason(reason)
                .decisionOffset(decisionOffset)
                .resetBadBinaryChecksum(resetBadBinaryChecksum)
                .earliestTime(earliestTime)
                .skipSignalReapply(skipSignalReapply)
        );
        return resp.getWorkflowRunId();
    }

}
