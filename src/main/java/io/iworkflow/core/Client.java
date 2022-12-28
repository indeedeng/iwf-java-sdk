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
     * return client
     *
     * @param registry      registry is required so that this client can perform some validation checks (workflow types, channel names)
     * @param clientOptions
     */
    public Client(final Registry registry, final ClientOptions clientOptions) {
        this.registry = registry;
        this.clientOptions = clientOptions;
        this.unregisteredClient = new UnregisteredClient(clientOptions);
    }

    public UnregisteredClient getUnregisteredClient() {
        return unregisteredClient;
    }

    public String startWorkflow(
            final Class<? extends Workflow> workflowClass,
            final String startStateId,
            final String workflowId,
            final WorkflowOptions options) {
        return startWorkflow(workflowClass, startStateId, null, workflowId, options);
    }

    public String startWorkflow(
            final Class<? extends Workflow> workflowClass,
            final String startStateId,
            final Object input,
            final String workflowId,
            final WorkflowOptions options) {
        final String wfType = workflowClass.getSimpleName();
        final StateDef stateDef = registry.getWorkflowState(wfType, startStateId);
        if (stateDef == null || !stateDef.getCanStartWorkflow()) {
            throw new IllegalArgumentException("invalid start stateId " + startStateId);
        }

        return unregisteredClient.startWorkflow(wfType, startStateId, input, workflowId, options);
    }

    public String startWorkflow(
            final Workflow workflow,
            final String startStateId,
            final Object input,
            final String workflowId,
            final WorkflowOptions options) {
        final String wfType = Registry.getWorkflowType(workflow);
        final StateDef stateDef = registry.getWorkflowState(wfType, startStateId);
        if (stateDef == null || !stateDef.getCanStartWorkflow()) {
            throw new IllegalArgumentException("invalid start stateId " + startStateId);
        }

        return unregisteredClient.startWorkflow(wfType, startStateId, input, workflowId, options);
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

        // if attribute keys is null or empty, iwf server will return all query attributes
        if (keys != null && !keys.isEmpty()) {
            List<String> nonExistingDataObjectKeyList = keys.stream()
                    .filter(s -> !queryDataObjectKeyToTypeMap.containsKey(s))
                    .collect(Collectors.toList());
            if (!nonExistingDataObjectKeyList.isEmpty()) {
                throw new IllegalArgumentException(
                        String.format(
                                "Query attributes not registered: %s",
                                String.join(", ", nonExistingDataObjectKeyList)
                        )
                );
            }
        }

        final WorkflowGetDataObjectsResponse response = unregisteredClient.getAnyWorkflowDataObjects(workflowId, workflowRunId, keys);

        if (response.getObjects() == null) {
            throw new InternalServiceException("query attributes not returned");
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
     * @param query
     * @param pageSize
     * @return
     */
    public WorkflowSearchResponse searchWorkflow(final String query, final int pageSize) {
        return unregisteredClient.searchWorkflow(query, pageSize);
    }

    /**
     * This search API support pagination
     *
     * @param request
     * @return
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

        // if attribute keys is null or empty, iwf server will return all query attributes
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
            throw new InternalServiceException("query attributes not returned");
        }
        Map<String, Object> result = new HashMap<>();
        for (SearchAttribute searchAttribute : response.getSearchAttributes()) {
            final SearchAttributeValueType saType = searchAttributeKeyToTypeMap.get(searchAttribute.getKey());
            Object value = getSearchAttributeValue(saType, searchAttribute);
            result.put(searchAttribute.getKey(), value);
        }
        return result;
    }

    private Object getSearchAttributeValue(final SearchAttributeValueType saType, final SearchAttribute searchAttribute) {
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
}
