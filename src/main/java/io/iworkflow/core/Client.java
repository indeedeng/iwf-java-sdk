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
import io.iworkflow.gen.models.WorkflowStateOptions;
import net.bytebuddy.ByteBuddy;
import net.bytebuddy.implementation.MethodDelegation;
import net.bytebuddy.matcher.ElementMatchers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static io.iworkflow.core.WorkflowState.shouldSkipWaitUntil;

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
     * createObject creates an DEObject
     *
     * @param objectDefinition is required
     * @param objectId         is required
     * @param timeoutSeconds   is required
     * @return objectExecutionId
     */
    public String createObject(
            final DEObject objectDefinition,
            final String objectId,
            final int timeoutSeconds) {
        return createObject(objectDefinition, objectId, timeoutSeconds, null, null);
    }

    /**
     * createObject creates an DEObject
     *
     * @param deObject       is required
     * @param objectId       is required
     * @param timeoutSeconds is required
     * @param input          is optional, can be null
     * @return objectExecutionId
     */
    public String createObject(
            final DEObject deObject,
            final String objectId,
            final int timeoutSeconds,
            final Object input) {
        return createObject(deObject, objectId, timeoutSeconds, input, null);
    }

    /**
     * createObject creates an DEObject
     *
     * @param deObject       is required
     * @param objectId       is required
     * @param timeoutSeconds is required
     * @param input          is optional, can be null
     * @param options        is optional, can be null
     * @return objectExecutionId
     */
    public String createObject(
            final DEObject deObject,
            final String objectId,
            final int timeoutSeconds,
            final Object input,
            final ObjectOptions options) {
        final String objectType = Registry.getObjectType(deObject);
        return doCreateObject(objectType, objectId, timeoutSeconds, input, options);
    }

    /**
     * createObject creates an DEObject
     *
     * @param deObjectClass  is required
     * @param objectId       is required
     * @param timeoutSeconds is required
     * @return objectExecutionId
     */
    public String createObject(
            final Class<? extends DEObject> deObjectClass,
            final String objectId,
            final int timeoutSeconds) {
        return createObject(deObjectClass, objectId, timeoutSeconds, null, null);
    }

    /**
     * createObject creates an DEObject
     *
     * @param deObjectClass  is required
     * @param objectId       is required
     * @param timeoutSeconds is required
     * @param input          is optional, can be null
     * @return objectExecutionId
     */
    public String createObject(
            final Class<? extends DEObject> deObjectClass,
            final String objectId,
            final int timeoutSeconds,
            final Object input) {
        return createObject(deObjectClass, objectId, timeoutSeconds, input, null);
    }

    /**
     * createObject creates an DEObject
     *
     * @param deObjectClass  is required
     * @param objectId       is required
     * @param timeoutSeconds is required
     * @param input          is optional, can be null
     * @param option         is optional, can be null
     * @return objectExecutionId
     */
    public String createObject(
            final Class<? extends DEObject> deObjectClass,
            final String objectId,
            final int timeoutSeconds,
            final Object input,
            final ObjectOptions option) {
        final String objectType = deObjectClass.getSimpleName();
        return doCreateObject(objectType, objectId, timeoutSeconds, input, option);
    }

    private String doCreateObject(
            final String objectType,
            final String objectId,
            final int timeoutSeconds,
            final Object input,
            final ObjectOptions options) {

        final ImmutableUnregisteredObjectOptions.Builder unregisterWorkflowOptions = ImmutableUnregisteredObjectOptions.builder();

        // validate
        final StateDef stateDef = registry.getWorkflowStartingState(objectType);
        final Class registeredInputType = stateDef.getWorkflowState().getInputType();
        if (input != null && !registeredInputType.isAssignableFrom(input.getClass())) {
            throw new WorkflowDefinitionException(String.format("input cannot be assigned to the starting state, input type: %s, starting state input type: %s", input.getClass(), registeredInputType));
        }

        WorkflowStateOptions stateOptions = stateDef.getWorkflowState().getStateOptions();
        if (shouldSkipWaitUntil(stateDef.getWorkflowState())) {
            if (stateOptions == null) {
                stateOptions = new WorkflowStateOptions().skipWaitUntil(true);
            } else {
                stateOptions.skipWaitUntil(true);
            }
        }
        if (stateOptions != null) {
            unregisterWorkflowOptions.startStateOptions(stateOptions);
        }
        if (options != null) {
            unregisterWorkflowOptions.objectIdReusePolicy(options.getObjectIdReusePolicy());
            unregisterWorkflowOptions.cronSchedule(options.getCronSchedule());
            unregisterWorkflowOptions.objectExecutionRetryPolicy(options.getObjectExecutionRetryPolicy());
            unregisterWorkflowOptions.objectConfigOverride(options.getObjectConfigOverride());

            final Map<String, SearchAttributeValueType> saTypes = registry.getSearchAttributeKeyToTypeMap(objectType);
            final List<SearchAttribute> convertedSAs = convertToSearchAttributeList(saTypes, options.getInitialSearchAttribute());
            unregisterWorkflowOptions.initialSearchAttribute(convertedSAs);
        }

        return unregisteredClient.startWorkflow(objectType, stateDef.getWorkflowState().getStateId(), objectId, timeoutSeconds, input, unregisterWorkflowOptions.build());
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
                        throw new IllegalStateException("unsupported type");
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
     * @param valueClass        the type class of the output
     * @param objectId          the objectId
     * @param objectExecutionId optional runId, can be empty
     * @param <T>               type of the output
     * @return the output result
     */
    public <T> T getSingleResultWithWait(
            Class<T> valueClass,
            final String objectId,
            final String objectExecutionId) {
        return unregisteredClient.getSingleResultWithWait(valueClass, objectId, objectExecutionId);
    }

    public <T> T getSingleResultWithWait(
            Class<T> valueClass,
            final String objectId) {
        return getSingleResultWithWait(valueClass, objectId, "");
    }

    /**
     * In some cases, a workflow may have more than one completion states
     *
     * @param objectId          objectId
     * @param objectExecutionId objectExecutionId
     * @return a list of the state output for completion states. User code will figure how to use ObjectEncoder to decode the output
     */
    public List<StateCompletionOutput> getMultiResultsWithWait(
            final String objectId, final String objectExecutionId) {
        return unregisteredClient.getMultiResultsWithWait(objectId, objectExecutionId);
    }

    public List<StateCompletionOutput> getMultiResultsWithWait(final String objectId) {
        return getMultiResultsWithWait(objectId, "");
    }

    public void sendSignal(
            final Class<? extends DEObject> deObjectClass,
            final String objectId,
            final String objectExecutionId,
            final String signalChannelName,
            final Object signalValue) {
        final String wfType = deObjectClass.getSimpleName();

        Map<String, Class<?>> nameToTypeMap = registry.getSignalChannelNameToSignalTypeMap(wfType);
        if (nameToTypeMap == null) {
            throw new IllegalArgumentException(
                    String.format("Object type %s is not registered", wfType)
            );
        }

        if (!nameToTypeMap.containsKey(signalChannelName)) {
            throw new IllegalArgumentException(String.format("Object type %s doesn't have signal %s", wfType, signalChannelName));
        }
        Class<?> signalType = nameToTypeMap.get(signalChannelName);
        if (signalValue != null && !signalType.isInstance(signalValue)) {
            throw new IllegalArgumentException(String.format("Signal value is not of type %s", signalType.getName()));
        }

        unregisteredClient.sendSignal(objectId, objectExecutionId, signalChannelName, signalValue);
    }

    /**
     * @param objectId            objectId
     * @param objectExecutionId   objectExecutionId
     * @param resetTypeAndOptions the combination parameter for reset
     * @return the new internal RunId after reset
     */
    public String resetObject(
            final String objectId,
            final String objectExecutionId,
            final ResetTypeAndOptions resetTypeAndOptions
    ) {

        return unregisteredClient.resetWorkflow(objectId, objectExecutionId, resetTypeAndOptions);
    }

    /**
     * close an object execution
     *
     * @param objectId          required
     * @param objectExecutionId optional, can be empty
     */
    public void closeObjectExecution(
            final String objectId,
            final String objectExecutionId) {
        unregisteredClient.closeObjectExecution(objectId, objectExecutionId);
    }

    /**
     * Stop a workflow with options
     *
     * @param objectId          required
     * @param objectExecutionId optional
     * @param options           optional
     */
    public void closeObjectExecution(
            final String objectId,
            final String objectExecutionId,
            final StopWorkflowOptions options) {
        unregisteredClient.closeObjectExecution(objectId, objectExecutionId, options);
    }

    public Map<String, Object> getDataAttributes(
            final Class<? extends DEObject> deObjectClass,
            final String objectId,
            final String objectExecutionId,
            List<String> keys) {
        if (keys == null || keys.isEmpty()) {
            throw new IllegalArgumentException("keys must contain at least one entry, or use getAllDataObjects API to get all");
        }
        return doGetWorkflowDataAttributes(deObjectClass, objectId, objectExecutionId, keys);
    }

    public Map<String, Object> getAllDataAttributes(
            final Class<? extends DEObject> deObjectClass,
            final String objectId,
            final String objectExecutionId) {
        return doGetWorkflowDataAttributes(deObjectClass, objectId, objectExecutionId, null);
    }

    private Map<String, Object> doGetWorkflowDataAttributes(
            final Class<? extends DEObject> deObjectClass,
            final String objectId,
            final String objectExecutionId,
            List<String> keys) {
        final String objectType = deObjectClass.getSimpleName();

        Map<String, Class<?>> queryDataObjectKeyToTypeMap = registry.getDataAttributeKeyToTypeMap(objectType);
        if (queryDataObjectKeyToTypeMap == null) {
            throw new IllegalArgumentException(
                    String.format("Workflow %s is not registered", objectType)
            );
        }

        // if attribute keys is null or empty, iwf server will return all data attributes
        if (keys != null && !keys.isEmpty()) {
            List<String> nonExistingDataObjectKeyList = keys.stream()
                    .filter(s -> !queryDataObjectKeyToTypeMap.containsKey(s))
                    .collect(Collectors.toList());
            if (!nonExistingDataObjectKeyList.isEmpty()) {
                throw new IllegalArgumentException(
                        String.format(
                                "data attributes not registered: %s",
                                String.join(", ", nonExistingDataObjectKeyList)
                        )
                );
            }
        }

        final WorkflowGetDataObjectsResponse response = unregisteredClient.getAnyDataAttributes(objectId, objectExecutionId, keys);

        if (response.getObjects() == null) {
            throw new IllegalStateException("data attributes not returned");
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
    public WorkflowSearchResponse searchObjects(final String query, final int pageSize) {
        return unregisteredClient.searcObjects(query, pageSize);
    }

    /**
     * This search API support pagination
     *
     * @param request the search request
     * @return the results of the search
     */
    public WorkflowSearchResponse searchObjects(final WorkflowSearchRequest request) {
        return unregisteredClient.searcObjects(request);
    }

    /**
     * create a new stub for invoking RPC
     *
     * @param objectClassForRpc the class of defining the RPCs to invoke
     * @param objectId          objectId is required
     * @param objectExecutionId optional
     * @param <T>               the class of defining the RPCs to invoke
     * @return the result of the RPC
     */
    public <T> T newRpcStub(Class<T> objectClassForRpc, String objectId, String objectExecutionId) {

        Class<?> dynamicType = new ByteBuddy()
                .subclass(objectClassForRpc)
                .method(ElementMatchers.any())
                .intercept(MethodDelegation.to(new RpcInvocationHandler(this.unregisteredClient, objectId, objectExecutionId)))
                .make()
                .load(getClass().getClassLoader())
                .getLoaded();

        T result;
        try {
            result = (T) dynamicType.newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
            throw new IllegalStateException(e);
        }

        return result;
    }

    /**
     * invoking the RPC through RPC stub
     *
     * @param rpcStubMethod the RPC method from stub created by {@link #newRpcStub(Class, String, String)}
     * @param input         the input of the RPC method
     * @param <I>           the input type
     * @param <O>           the output type
     * @return output
     */
    public <I, O> O invokeRPC(RpcDefinitions.RpcFunc1<I, O> rpcStubMethod, I input) {
        return rpcStubMethod.execute(null, input, null, null);
    }

    /**
     * invoking the RPC through RPC stub
     *
     * @param rpcStubMethod the RPC method from stub created by {@link #newRpcStub(Class, String, String)}
     * @param <O>           the output type
     * @return output
     */
    public <O> O invokeRPC(RpcDefinitions.RpcFunc0<O> rpcStubMethod) {
        return rpcStubMethod.execute(null, null, null);
    }

    /**
     * invoking the RPC through RPC stub
     *
     * @param rpcStubMethod the RPC method from stub created by {@link #newRpcStub(Class, String, String)}
     * @param input         the input of the RPC method
     * @param <I>           the input type
     */
    public <I> void invokeRPC(RpcDefinitions.RpcProc1<I> rpcStubMethod, I input) {
        rpcStubMethod.execute(null, input, null, null);
    }

    /**
     * invoking the RPC through RPC stub
     *
     * @param rpcStubMethod the RPC method from stub created by {@link #newRpcStub(Class, String, String)}
     */
    public void invokeRPC(RpcDefinitions.RpcProc0 rpcStubMethod) {
        rpcStubMethod.execute(null, null, null);
    }

    public Map<String, Object> getSearchAttributes(
            final Class<? extends DEObject> deObjectClass,
            final String objectId,
            final String objectExecutionId,
            List<String> attributeKeys) {
        if (attributeKeys == null || attributeKeys.isEmpty()) {
            throw new IllegalArgumentException("attributeKeys must contain at least one entry, or use GetAllSearchAttributes API to get all");
        }
        return doGetSearchAttributes(deObjectClass, objectId, objectExecutionId, attributeKeys);
    }

    public Map<String, Object> getAllSearchAttributes(
            final Class<? extends DEObject> deObjectClass,
            final String objectId,
            final String objectExecutionId) {
        return doGetSearchAttributes(deObjectClass, objectId, objectExecutionId, null);
    }

    private Map<String, Object> doGetSearchAttributes(
            final Class<? extends DEObject> deObjectClass,
            final String objectId,
            final String objectExecutionId,
            final List<String> attributeKeys) {
        final String wfType = deObjectClass.getSimpleName();

        final Map<String, SearchAttributeValueType> searchAttributeKeyToTypeMap = registry.getSearchAttributeKeyToTypeMap(wfType);
        if (searchAttributeKeyToTypeMap == null) {
            throw new IllegalArgumentException(
                    String.format("Workflow %s is not registered", wfType)
            );
        }

        // if attribute keys is null or empty, iwf server will return all data attributes
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

        WorkflowGetSearchAttributesResponse response = unregisteredClient.getAnyWorkflowSearchAttributes(objectId, objectExecutionId, keyAndTypes);

        if (response.getSearchAttributes() == null) {
            throw new IllegalStateException("search attributes not returned");
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
                throw new IllegalStateException("unsupported type");
        }
    }

    public void skipTimer(
            final String objectId,
            final String objectExecutionId,
            final Class<? extends WorkflowState> stateClass,
            final int stateExecutionNumber,
            final String timerCommandId) {
        skipTimer(objectId, objectExecutionId, stateClass.getSimpleName(), stateExecutionNumber, timerCommandId);
    }

    public void skipTimer(
            final String objectId,
            final String objectExecutionId,
            final String workflowStateId,
            final int stateExecutionNumber,
            final String timerCommandId) {
        unregisteredClient.skipTimer(objectId, objectExecutionId, workflowStateId, stateExecutionNumber, timerCommandId);
    }

    public void skipTimer(
            final String objectId,
            final String objectExecutionId,
            final Class<? extends WorkflowState> stateClass,
            final int stateExecutionNumber,
            final int timerCommandIndex) {
        skipTimer(objectId, objectExecutionId, stateClass.getSimpleName(), stateExecutionNumber, timerCommandIndex);
    }

    public void skipTimer(
            final String objectId,
            final String objectExecutionId,
            final String workflowStateId,
            final int stateExecutionNumber,
            final int timerCommandIndex) {
        unregisteredClient.skipTimer(objectId, objectExecutionId, workflowStateId, stateExecutionNumber, timerCommandIndex);
    }
}
