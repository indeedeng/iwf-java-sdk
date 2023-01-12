package io.iworkflow.core;

import io.iworkflow.gen.models.KeyValue;
import io.iworkflow.gen.models.SearchAttribute;
import io.iworkflow.gen.models.SearchAttributeKeyAndType;
import io.iworkflow.gen.models.SearchAttributeValueType;
import io.iworkflow.gen.models.StateCompletionOutput;
import io.iworkflow.gen.models.WorkflowGetDataObjectsResponse;
import io.iworkflow.gen.models.WorkflowGetSearchAttributesResponse;
import io.iworkflow.gen.models.WorkflowSearchRequest;
import io.iworkflow.gen.models.WorkflowSearchResponse;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class Client {
    private final Registry registry;

    private final UnregisteredClient unregisteredClient;

    final ClientOptions clientOptions;

    /**
     * return a full featured client. If you don't have the workflow Registry, you should use {@link UnregisteredClient} instead
     *
     * @param registry      registry is required so that this client can perform some validation checks (workflow types, channel names)
     * @param clientOptions is for configuring the client
     */
    public Client(final Registry registry, final ClientOptions clientOptions) {
        this.registry = registry;
        this.clientOptions = clientOptions;
        this.unregisteredClient = new UnregisteredClient(clientOptions);
    }

    public UnregisteredClient getUnregisteredClient() {
        return unregisteredClient;
    }

    /**
     * startWorkflow starts a workflow execution
     *
     * @param workflow               is required
     * @param workflowId             is required
     * @param workflowTimeoutSeconds is required
     * @return runId
     */
    public String startWorkflow(
            final Workflow workflow,
            final String workflowId,
            final int workflowTimeoutSeconds) {
        return startWorkflow(workflow, workflowId, workflowTimeoutSeconds, null, null);
    }

    /**
     * startWorkflow starts a workflow execution
     *
     * @param workflow               is required
     * @param workflowId             is required
     * @param workflowTimeoutSeconds is required
     * @param input                  is optional, can be null
     * @return runId
     */
    public String startWorkflow(
            final Workflow workflow,
            final String workflowId,
            final int workflowTimeoutSeconds,
            final Object input) {
        return startWorkflow(workflow, workflowId, workflowTimeoutSeconds, input, null);
    }

    /**
     * startWorkflow starts a workflow execution
     *
     * @param workflow               is required
     * @param workflowId             is required
     * @param workflowTimeoutSeconds is required
     * @param input                  is optional, can be null
     * @param options                is optional, can be null
     * @return runId
     */
    public String startWorkflow(
            final Workflow workflow,
            final String workflowId,
            final int workflowTimeoutSeconds,
            final Object input,
            final WorkflowOptions options) {
        final String wfType = Registry.getWorkflowType(workflow);
        return doStartWorkflow(wfType, workflowId, workflowTimeoutSeconds, input, options);
    }

    /**
     * startWorkflow starts a workflow execution
     *
     * @param workflowClass          is required
     * @param workflowId             is required
     * @param workflowTimeoutSeconds is required
     * @return runId
     */
    public String startWorkflow(
            final Class<? extends Workflow> workflowClass,
            final String workflowId,
            final int workflowTimeoutSeconds) {
        return startWorkflow(workflowClass, workflowId, workflowTimeoutSeconds, null, null);
    }

    /**
     * startWorkflow starts a workflow execution
     *
     * @param workflowClass          is required
     * @param workflowId             is required
     * @param workflowTimeoutSeconds is required
     * @param input                  is optional, can be null
     * @return runId
     */
    public String startWorkflow(
            final Class<? extends Workflow> workflowClass,
            final String workflowId,
            final int workflowTimeoutSeconds,
            final Object input) {
        return startWorkflow(workflowClass, workflowId, workflowTimeoutSeconds, input, null);
    }

    /**
     * startWorkflow starts a workflow execution
     *
     * @param workflowClass          is required
     * @param workflowId             is required
     * @param workflowTimeoutSeconds is required
     * @param input                  is optional, can be null
     * @param option                 is optional, can be null
     * @return runId
     */
    public String startWorkflow(
            final Class<? extends Workflow> workflowClass,
            final String workflowId,
            final int workflowTimeoutSeconds,
            final Object input,
            final WorkflowOptions option) {
        final String wfType = workflowClass.getSimpleName();
        return doStartWorkflow(wfType, workflowId, workflowTimeoutSeconds, input, option);
    }

    private String doStartWorkflow(
            final String wfType,
            final String workflowId,
            final int workflowTimeoutSeconds,
            final Object input,
            final WorkflowOptions options) {

        final ImmutableUnregisteredWorkflowOptions.Builder unregisterWorkflowOptions = ImmutableUnregisteredWorkflowOptions.builder();

        // validate
        final StateDef stateDef = registry.getWorkflowStartingState(wfType);
        final Class registeredInputType = stateDef.getWorkflowState().getInputType();
        if (input != null && !input.getClass().isAssignableFrom(registeredInputType)) {
            throw new WorkflowDefinitionException(String.format("input cannot be assigned to the starting state, input type: %s, starting state input type: %s", input.getClass(), registeredInputType.getClass()));
        }

        if (stateDef.getWorkflowState().getStateOptions() != null) {
            unregisterWorkflowOptions.startStateOptions(stateDef.getWorkflowState().getStateOptions());
        }
        if (options != null) {
            unregisterWorkflowOptions.workflowIdReusePolicy(options.getWorkflowIdReusePolicy());
            unregisterWorkflowOptions.cronSchedule(options.getCronSchedule());
            unregisterWorkflowOptions.workflowRetryPolicy(options.getWorkflowRetryPolicy());

            final Map<String, SearchAttributeValueType> saTypes = registry.getSearchAttributeKeyToTypeMap(wfType);
            final List<SearchAttribute> convertedSAs = convertToSearchAttributeList(saTypes, options.getInitialSearchAttribute());
            unregisterWorkflowOptions.initialSearchAttribute(convertedSAs);
        }

        return unregisteredClient.startWorkflow(wfType, stateDef.getWorkflowState().getStateId(), workflowId, workflowTimeoutSeconds, input, unregisterWorkflowOptions.build());
    }

    private List<SearchAttribute> convertToSearchAttributeList(final Map<String, SearchAttributeValueType> saTypes, final Map<String, Object> initialSearchAttribute) {
        List<SearchAttribute> convertedSAs = new ArrayList<>();
        if (initialSearchAttribute.size() > 0) {
            initialSearchAttribute.forEach((saKey, val) -> {
                if (!saTypes.containsKey(saKey)) {
                    throw new WorkflowDefinitionException(String.format("key %s is not defined as search attribute, all keys are: %s ", saKey, saTypes.keySet()));
                }
                final SearchAttributeValueType valType = saTypes.get(saKey);
                final SearchAttribute newSa = new SearchAttribute().key(saKey).valueType(valType);
                boolean isValCorrectType = false;
                switch (valType) {
                    case INT:
                        if (val instanceof Integer) {
                            Long lVal = ((Integer) val).longValue();
                            convertedSAs.add(newSa.integerValue(lVal));
                            isValCorrectType = true;
                        }
                        if (val instanceof Long) {
                            convertedSAs.add(newSa.integerValue((Long) val));
                            isValCorrectType = true;
                        }
                        break;
                    case DOUBLE:
                        if (val instanceof Float) {
                            Double lVal = ((Float) val).doubleValue();
                            convertedSAs.add(newSa.doubleValue(lVal));
                            isValCorrectType = true;
                        }
                        if (val instanceof Double) {
                            convertedSAs.add(new SearchAttribute().doubleValue((Double) val));
                            isValCorrectType = true;
                        }
                        break;
                    case BOOL:
                        if (val instanceof Boolean) {
                            convertedSAs.add(newSa.boolValue((Boolean) val));
                            isValCorrectType = true;
                        }
                        break;
                    case KEYWORD:
                    case TEXT:
                    case DATETIME:
                        if (val instanceof String) {
                            convertedSAs.add(newSa.stringValue((String) val));
                            isValCorrectType = true;
                        }
                        break;
                    case KEYWORD_ARRAY:
                        if (val instanceof List) {
                            final List<String> listArr = (List<String>) val;
                            convertedSAs.add(newSa.stringArrayValue(listArr));
                            isValCorrectType = true;
                        }
                        break;
                    default:
                        throw new InternalServiceException("unsupported type");
                }

                if (!isValCorrectType) {
                    throw new IllegalArgumentException(String.format("search attribute value is not set correctly for key %s with value type %s", saKey, valType));
                }
            });
        }
        return convertedSAs;
    }

    /**
     * For most cases, a workflow only has one result(one completion state)
     * Use this API to retrieve the output of the state
     *
     * @param valueClass    the type class of the output
     * @param workflowId    the workflowId
     * @param workflowRunId optional runId, can be empty
     * @param <T>           type of the output
     * @return the output result
     */
    public <T> T getSimpleWorkflowResultWithWait(
            Class<T> valueClass,
            final String workflowId,
            final String workflowRunId) {
        return unregisteredClient.getSimpleWorkflowResultWithWait(valueClass, workflowId, workflowRunId);
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
     * @param workflowRunId workflowRunId
     * @return a list of the state output for completion states. User code will figure how to use ObjectEncoder to decode the output
     */
    public List<StateCompletionOutput> getComplexWorkflowResultWithWait(
            final String workflowId, final String workflowRunId) {
        return unregisteredClient.getComplexWorkflowResultWithWait(workflowId, workflowRunId);
    }

    public List<StateCompletionOutput> getComplexWorkflowResultWithWait(final String workflowId) {
        return getComplexWorkflowResultWithWait(workflowId, "");
    }

    public void signalWorkflow(
            final Class<? extends Workflow> workflowClass,
            final String workflowId,
            final String workflowRunId,
            final String signalChannelName,
            final Object signalValue) {
        final String wfType = workflowClass.getSimpleName();

        Map<String, Class<?>> nameToTypeMap = registry.getSignalChannelNameToSignalTypeMap(wfType);
        if (nameToTypeMap == null) {
            throw new IllegalArgumentException(
                    String.format("Workflow %s is not registered", wfType)
            );
        }

        if (!nameToTypeMap.containsKey(signalChannelName)) {
            throw new IllegalArgumentException(String.format("Workflow %s doesn't have signal %s", wfType, signalChannelName));
        }
        Class<?> signalType = nameToTypeMap.get(signalChannelName);
        if (!signalType.isInstance(signalValue)) {
            throw new IllegalArgumentException(String.format("Signal value is not of type %s", signalType.getName()));
        }

        unregisteredClient.signalWorkflow(workflowId, workflowRunId, signalChannelName, signalValue);
    }

    /**
     * @param workflowId workflowId
     * @param workflowRunId workflowRunId
     * @param resetWorkflowTypeAndOptions the combination parameter for reset
     * @return the new runId after reset
     */
    public String resetWorkflow(
            final String workflowId,
            final String workflowRunId,
            final ResetWorkflowTypeAndOptions resetWorkflowTypeAndOptions
    ) {

        return unregisteredClient.resetWorkflow(workflowId, workflowRunId, resetWorkflowTypeAndOptions);
    }

    /**
     * Stop a workflow, this is essentially terminate the workflow gracefully
     *
     * @param workflowId    required
     * @param workflowRunId optional, can be empty
     */
    public void stopWorkflow(
            final String workflowId,
            final String workflowRunId) {
        unregisteredClient.StopWorkflow(workflowId, workflowRunId);
    }

    public Map<String, Object> getWorkflowDataObjects(
            final Class<? extends Workflow> workflowClass,
            final String workflowId,
            final String workflowRunId,
            List<String> keys) {
        if (keys == null || keys.isEmpty()) {
            throw new IllegalArgumentException("keys must contain at least one entry, or use getAllDataObjects API to get all");
        }
        return doGetWorkflowDataObjects(workflowClass, workflowId, workflowRunId, keys);
    }

    public Map<String, Object> getAllDataObjects(
            final Class<? extends Workflow> workflowClass,
            final String workflowId,
            final String workflowRunId) {
        return doGetWorkflowDataObjects(workflowClass, workflowId, workflowRunId, null);
    }

    private Map<String, Object> doGetWorkflowDataObjects(
            final Class<? extends Workflow> workflowClass,
            final String workflowId,
            final String workflowRunId,
            List<String> keys) {
        final String wfType = workflowClass.getSimpleName();

        Map<String, Class<?>> queryDataObjectKeyToTypeMap = registry.getDataObjectKeyToTypeMap(wfType);
        if (queryDataObjectKeyToTypeMap == null) {
            throw new IllegalArgumentException(
                    String.format("Workflow %s is not registered", wfType)
            );
        }

        // if attribute keys is null or empty, iwf server will return all data objects
        if (keys != null && !keys.isEmpty()) {
            List<String> nonExistingDataObjectKeyList = keys.stream()
                    .filter(s -> !queryDataObjectKeyToTypeMap.containsKey(s))
                    .collect(Collectors.toList());
            if (!nonExistingDataObjectKeyList.isEmpty()) {
                throw new IllegalArgumentException(
                        String.format(
                                "data objects not registered: %s",
                                String.join(", ", nonExistingDataObjectKeyList)
                        )
                );
            }
        }

        final WorkflowGetDataObjectsResponse response = unregisteredClient.getAnyWorkflowDataObjects(workflowId, workflowRunId, keys);

        if (response.getObjects() == null) {
            throw new InternalServiceException("data objects not returned");
        }
        Map<String, Object> result = new HashMap<>();
        for (KeyValue keyValue : response.getObjects()) {
            if (keyValue.getValue() != null) {
                result.put(
                        keyValue.getKey(),
                        clientOptions.getObjectEncoder().decode(keyValue.getValue(), queryDataObjectKeyToTypeMap.get(keyValue.getKey()))
                );
            }
        }
        return result;
    }

    /**
     * This is a simplified API to search without pagination, use the other searchWorkflow API for pagination feature
     *
     * @param query    the query of the search, see Cadence/Temporal search attributes doc
     * @param pageSize the page size
     * @return the results of the search, this will only return one page of the results
     */
    public WorkflowSearchResponse searchWorkflow(final String query, final int pageSize) {
        return unregisteredClient.searchWorkflow(query, pageSize);
    }

    /**
     * This search API support pagination
     *
     * @param request the search request
     * @return the results of the search
     */
    public WorkflowSearchResponse searchWorkflow(final WorkflowSearchRequest request) {
        return unregisteredClient.searchWorkflow(request);
    }

    public Map<String, Object> getWorkflowSearchAttributes(
            final Class<? extends Workflow> workflowClass,
            final String workflowId,
            final String workflowRunId,
            List<String> attributeKeys) {
        if (attributeKeys == null || attributeKeys.isEmpty()) {
            throw new IllegalArgumentException("attributeKeys must contain at least one entry, or use GetAllSearchAttributes API to get all");
        }
        return doGetWorkflowSearchAttributes(workflowClass, workflowId, workflowRunId, attributeKeys);
    }

    public Map<String, Object> getAllSearchAttributes(
            final Class<? extends Workflow> workflowClass,
            final String workflowId,
            final String workflowRunId) {
        return doGetWorkflowSearchAttributes(workflowClass, workflowId, workflowRunId, null);
    }

    private Map<String, Object> doGetWorkflowSearchAttributes(
            final Class<? extends Workflow> workflowClass,
            final String workflowId,
            final String workflowRunId,
            final List<String> attributeKeys) {
        final String wfType = workflowClass.getSimpleName();

        final Map<String, SearchAttributeValueType> searchAttributeKeyToTypeMap = registry.getSearchAttributeKeyToTypeMap(wfType);
        if (searchAttributeKeyToTypeMap == null) {
            throw new IllegalArgumentException(
                    String.format("Workflow %s is not registered", wfType)
            );
        }

        // if attribute keys is null or empty, iwf server will return all data objects
        if (attributeKeys != null && !attributeKeys.isEmpty()) {
            List<String> nonExistingSearchAttributeList = attributeKeys.stream()
                    .filter(s -> !searchAttributeKeyToTypeMap.containsKey(s))
                    .collect(Collectors.toList());

            if (!nonExistingSearchAttributeList.isEmpty()) {
                throw new IllegalArgumentException(
                        String.format(
                                "Search attributes not registered: %s",
                                String.join(", ", nonExistingSearchAttributeList)
                        )
                );
            }
        }

        List<SearchAttributeKeyAndType> keyAndTypes = new ArrayList<>();
        if (attributeKeys == null) {
            searchAttributeKeyToTypeMap.forEach((key, type) -> {
                final SearchAttributeKeyAndType keyAndType = new SearchAttributeKeyAndType()
                        .key(key)
                        .valueType(type);
                keyAndTypes.add(keyAndType);
            });
        } else {
            attributeKeys.forEach((key) -> {
                final SearchAttributeValueType saType = searchAttributeKeyToTypeMap.get(key);
                final SearchAttributeKeyAndType keyAndType = new SearchAttributeKeyAndType()
                        .key(key)
                        .valueType(saType);
                keyAndTypes.add(keyAndType);
            });
        }

        WorkflowGetSearchAttributesResponse response = unregisteredClient.getAnyWorkflowSearchAttributes(workflowId, workflowRunId, keyAndTypes);

        if (response.getSearchAttributes() == null) {
            throw new InternalServiceException("data objects not returned");
        }
        Map<String, Object> result = new HashMap<>();
        for (SearchAttribute searchAttribute : response.getSearchAttributes()) {
            final SearchAttributeValueType saType = searchAttributeKeyToTypeMap.get(searchAttribute.getKey());
            Object value = getSearchAttributeValue(saType, searchAttribute);
            result.put(searchAttribute.getKey(), value);
        }
        return result;
    }

    static Object getSearchAttributeValue(final SearchAttributeValueType saType, final SearchAttribute searchAttribute) {
        switch (saType) {
            case INT:
                return searchAttribute.getIntegerValue();
            case DOUBLE:
                return searchAttribute.getDoubleValue();
            case BOOL:
                return searchAttribute.getBoolValue();
            case KEYWORD:
            case TEXT:
            case DATETIME:
                return searchAttribute.getStringValue();
            case KEYWORD_ARRAY:
                return searchAttribute.getStringArrayValue();
            default:
                throw new InternalServiceException("unsupported type");
        }
    }

    public void skipTimer(
            final String workflowId,
            final String workflowRunId,
            final Class<? extends WorkflowState> stateClass,
            final int stateExecutionNumber,
            final String timerCommandId) {
        skipTimer(workflowId, workflowRunId, stateClass.getSimpleName(), stateExecutionNumber, timerCommandId);
    }

    public void skipTimer(
            final String workflowId,
            final String workflowRunId,
            final String workflowStateId,
            final int stateExecutionNumber,
            final String timerCommandId) {
        unregisteredClient.skipTimer(workflowId, workflowRunId, workflowStateId, stateExecutionNumber, timerCommandId);
    }

    public void skipTimer(
            final String workflowId,
            final String workflowRunId,
            final Class<? extends WorkflowState> stateClass,
            final int stateExecutionNumber,
            final int timerCommandIndex) {
        skipTimer(workflowId, workflowRunId, stateClass.getSimpleName(), stateExecutionNumber, timerCommandIndex);
    }

    public void skipTimer(
            final String workflowId,
            final String workflowRunId,
            final String workflowStateId,
            final int stateExecutionNumber,
            final int timerCommandIndex) {
        unregisteredClient.skipTimer(workflowId, workflowRunId, workflowStateId, stateExecutionNumber, timerCommandIndex);
    }
}
