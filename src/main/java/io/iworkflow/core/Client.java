package io.iworkflow.core;

import feign.FeignException;
import io.iworkflow.core.persistence.PersistenceOptions;
import io.iworkflow.gen.models.KeyValue;
import io.iworkflow.gen.models.SearchAttribute;
import io.iworkflow.gen.models.SearchAttributeKeyAndType;
import io.iworkflow.gen.models.SearchAttributeValueType;
import io.iworkflow.gen.models.StateCompletionOutput;
import io.iworkflow.gen.models.WorkflowGetDataObjectsResponse;
import io.iworkflow.gen.models.WorkflowGetResponse;
import io.iworkflow.gen.models.WorkflowGetSearchAttributesResponse;
import io.iworkflow.gen.models.WorkflowSearchRequest;
import io.iworkflow.gen.models.WorkflowSearchResponse;
import io.iworkflow.gen.models.WorkflowStateOptions;
import net.bytebuddy.ByteBuddy;
import net.bytebuddy.implementation.MethodDelegation;
import net.bytebuddy.matcher.ElementMatchers;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
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
     * startWorkflow starts a workflow execution
     *
     * @param workflowClass          is required
     * @param workflowId             is required
     * @param workflowTimeoutSeconds is required
     * @return runId
     */
    public String startWorkflow(
            final Class<? extends ObjectWorkflow> workflowClass,
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
            final Class<? extends ObjectWorkflow> workflowClass,
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
            final Class<? extends ObjectWorkflow> workflowClass,
            final String workflowId,
            final int workflowTimeoutSeconds,
            final Object input,
            final WorkflowOptions option) {
        final String wfType = workflowClass.getSimpleName();
        return startWorkflow(wfType, workflowId, workflowTimeoutSeconds, input, option);
    }

    /**
     * startWorkflow starts a workflow execution
     *
     * @param wfType                 is required. It should be the same as the {@link ObjectWorkflow#getWorkflowType()}
     * @param workflowId             is required
     * @param workflowTimeoutSeconds is required
     * @param input                  is optional, can be null
     * @param options                is optional, can be null
     * @return runId
     */
    public String startWorkflow(
            final String wfType,
            final String workflowId,
            final int workflowTimeoutSeconds,
            final Object input,
            final WorkflowOptions options) {
        checkWorkflowTypeExists(wfType);

        final ImmutableUnregisteredWorkflowOptions.Builder unregisterWorkflowOptions = ImmutableUnregisteredWorkflowOptions.builder();

        if (options != null) {
            unregisterWorkflowOptions.workflowIdReusePolicy(options.getWorkflowIdReusePolicy());
            unregisterWorkflowOptions.cronSchedule(options.getCronSchedule());
            unregisterWorkflowOptions.workflowRetryPolicy(options.getWorkflowRetryPolicy());
            unregisterWorkflowOptions.workflowConfigOverride(options.getWorkflowConfigOverride());
            unregisterWorkflowOptions.waitForCompletionStateExecutionIds(options.getWaitForCompletionStateExecutionIds());

            final Map<String, SearchAttributeValueType> saTypes = registry.getSearchAttributeKeyToTypeMap(wfType);
            final List<SearchAttribute> convertedSAs = convertToSearchAttributeList(saTypes, options.getInitialSearchAttribute());
            unregisterWorkflowOptions.initialSearchAttribute(convertedSAs);
        }

        final Optional<StateDef> stateDefOptional = registry.getWorkflowStartingState(wfType);
        String startStateId = null;
        if (stateDefOptional.isPresent()) {
            StateDef stateDef = stateDefOptional.get();
            startStateId = stateDef.getWorkflowState().getStateId();

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
        }

        final PersistenceOptions schemaOptions = registry.getPersistenceOptions(wfType);
        if (schemaOptions.getEnableCaching()) {
            unregisterWorkflowOptions.usingMemoForDataAttributes(schemaOptions.getEnableCaching());
        }

        return unregisteredClient.startWorkflow(wfType, startStateId, workflowId, workflowTimeoutSeconds, input, unregisterWorkflowOptions.build());
    }

    private void checkWorkflowTypeExists(String wfType) {
        final ObjectWorkflow wf = registry.getWorkflow(wfType);
        if (wf == null) {
            throw new IllegalArgumentException("Workflow " + wfType + " is not registered");
        }
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
     * For most cases, a workflow only has one result(one completion state).
     * Use this API to retrieve the output of the state with waiting for the workflow to complete.
     * If the workflow is not COMPLETED, throw the {@link feign.FeignException.FeignClientException}.
     *
     * @param valueClass    required, the type class of the output
     * @param workflowId    required, the workflowId
     * @param workflowRunId optional, can be empty
     * @param <T>           type of the output
     * @return the output result
     */
    public <T> T getSimpleWorkflowResultWithWait(
            final Class<T> valueClass,
            final String workflowId,
            final String workflowRunId) {
        return unregisteredClient.getSimpleWorkflowResultWithWait(valueClass, workflowId, workflowRunId);
    }

    /**
     * For most cases, a workflow only has one result(one completion state).
     * Use this API to retrieve the output of the state with waiting for the workflow to complete.
     * If the workflow is not COMPLETED, throw the {@link feign.FeignException.FeignClientException}.
     *
     * @param valueClass    required, the type class of the output
     * @param workflowId    required, the workflowId
     * @param <T>           type of the output
     * @return the output result
     */
    public <T> T getSimpleWorkflowResultWithWait(
            final Class<T> valueClass,
            final String workflowId) {
        return getSimpleWorkflowResultWithWait(valueClass, workflowId, "");
    }

    /**
     * For most cases, a workflow only has one result(one completion state).
     * Use this API to retrieve the output of the state without waiting for the workflow to complete.
     * If the workflow is not COMPLETED, throw the {@link WorkflowUncompletedException}.
     * Else, return the same result as {@link #getSimpleWorkflowResultWithWait(Class, String, String)}.
     *
     * @param valueClass    required, the type class of the output
     * @param workflowId    required, the workflowId
     * @param workflowRunId optional, can be empty
     * @param <T>           type of the output
     * @return the output result
     */
    public <T> T tryGettingSimpleWorkflowResult(
            final Class<T> valueClass,
            final String workflowId,
            final String workflowRunId) {
        return unregisteredClient.tryGettingSimpleWorkflowResult(valueClass, workflowId, workflowRunId);
    }

    /**
     * For most cases, a workflow only has one result(one completion state).
     * Use this API to retrieve the output of the state without waiting for the workflow to complete.
     * If the workflow is not COMPLETED, throw the {@link WorkflowUncompletedException}.
     * Else, return the same result as {@link #getSimpleWorkflowResultWithWait(Class, String)}.
     *
     * @param valueClass    required, the type class of the output
     * @param workflowId    required, the workflowId
     * @param <T>           type of the output
     * @return the output result
     */
    public <T> T tryGettingSimpleWorkflowResult(
            final Class<T> valueClass,
            final String workflowId) {
        return tryGettingSimpleWorkflowResult(valueClass, workflowId, "");
    }

    /**
     * In some cases, a workflow may have more than one completion states.
     * Use this API to retrieve the output of the states with waiting for the workflow to complete.
     * If the workflow is not COMPLETED, throw the {@link feign.FeignException.FeignClientException}.
     *
     * @param workflowId    required, the workflowId
     * @param workflowRunId optional, can be empty
     * @return a list of the state output for completion states. User code will figure how to use ObjectEncoder to decode the output
     */
    public List<StateCompletionOutput> getComplexWorkflowResultWithWait(
            final String workflowId, final String workflowRunId) {
        return unregisteredClient.getComplexWorkflowResultWithWait(workflowId, workflowRunId);
    }

    /**
     * In some cases, a workflow may have more than one completion states.
     * Use this API to retrieve the output of the states with waiting for the workflow to complete.
     * If the workflow is not COMPLETED, throw the {@link feign.FeignException.FeignClientException}.
     *
     * @param workflowId    required, the workflowId
     * @return a list of the state output for completion states. User code will figure how to use ObjectEncoder to decode the output
     */
    public List<StateCompletionOutput> getComplexWorkflowResultWithWait(final String workflowId) {
        return getComplexWorkflowResultWithWait(workflowId, "");
    }

    /**
     * In some cases, a workflow may have more than one completion states.
     * Use this API to retrieve the output of the states without waiting for the workflow to complete.
     * If the workflow is not COMPLETED, throw the {@link WorkflowUncompletedException}.
     * Else, return the same result as {@link #getComplexWorkflowResultWithWait(String, String)}.
     *
     * @param workflowId    required, the workflowId
     * @param workflowRunId optional, can be empty
     * @return a list of the state output for completion states. User code will figure how to use ObjectEncoder to decode the output
     */
    public List<StateCompletionOutput> tryGettingComplexWorkflowResult(
            final String workflowId, final String workflowRunId) {
        return unregisteredClient.tryGettingComplexWorkflowResult(workflowId, workflowRunId);
    }

    /**
     * In some cases, a workflow may have more than one completion states.
     * Use this API to retrieve the output of the states without waiting for the workflow to complete.
     * If the workflow is not COMPLETED, throw the {@link WorkflowUncompletedException}.
     * Else, return the same result as {@link #getComplexWorkflowResultWithWait(String)}.
     *
     * @param workflowId    required, the workflowId
     * @return a list of the state output for completion states. User code will figure how to use ObjectEncoder to decode the output
     */
    public List<StateCompletionOutput> tryGettingComplexWorkflowResult(final String workflowId) {
        return tryGettingComplexWorkflowResult(workflowId, "");
    }

    /**
     * Emit a signal message for the workflow object to receive from external sources
     *
     * @param workflowClass     required
     * @param workflowId        required
     * @param workflowRunId     optional, can be empty
     * @param signalChannelName required
     * @param signalValue       optional, can be null
     */
    public void signalWorkflow(
            final Class<? extends ObjectWorkflow> workflowClass,
            final String workflowId,
            final String workflowRunId,
            final String signalChannelName,
            final Object signalValue) {
        final String wfType = workflowClass.getSimpleName();

        checkWorkflowTypeExists(wfType);

        final Class<?> signalType = registry.getSignalChannelTypeStore(wfType).getType(signalChannelName);

        if (signalValue != null && !signalType.isInstance(signalValue)) {
            throw new IllegalArgumentException(String.format("Signal value is not of type %s", signalType.getName()));
        }

        unregisteredClient.signalWorkflow(workflowId, workflowRunId, signalChannelName, signalValue);
    }

    /**
     * Emit a signal message for the workflow object to receive from external sources
     *
     * @param workflowClass     required
     * @param workflowId        required
     * @param signalChannelName required
     * @param signalValue       optional, can be null
     */
    public void signalWorkflow(
            final Class<? extends ObjectWorkflow> workflowClass,
            final String workflowId,
            final String signalChannelName,
            final Object signalValue) {
        signalWorkflow(workflowClass, workflowId, "", signalChannelName, signalValue);
    }

    /**
     * @param workflowId                    required
     * @param workflowRunId                 optional, can be empty
     * @param resetWorkflowTypeAndOptions   required, the combination parameter for reset
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
     * @param workflowId                    required
     * @param resetWorkflowTypeAndOptions   required, the combination parameter for reset
     * @return the new runId after reset
     */
    public String resetWorkflow(
            final String workflowId,
            final ResetWorkflowTypeAndOptions resetWorkflowTypeAndOptions
    ) {
        return resetWorkflow(workflowId, "", resetWorkflowTypeAndOptions);
    }

    /**
     * Stop a workflow with options
     *
     * @param workflowId    required
     * @param workflowRunId optional, can be empty
     * @param options       optional, can be null. If not set, the workflow status will be CANCELED
     */
    public void stopWorkflow(
            final String workflowId,
            final String workflowRunId,
            final StopWorkflowOptions options) {
        unregisteredClient.stopWorkflow(workflowId, workflowRunId, options);
    }

    /**
     * Stop a workflow with options
     *
     * @param workflowId    required
     * @param options       optional, can be null. If not set, the workflow status will be CANCELED
     */
    public void stopWorkflow(
            final String workflowId,
            final StopWorkflowOptions options) {
        stopWorkflow(workflowId, "", options);
    }

    /**
     * Stop a workflow, this is essentially terminate the workflow gracefully
     * The workflow status will be CANCELED
     *
     * @param workflowId    required
     */
    public void stopWorkflow(final String workflowId) {
        stopWorkflow(workflowId, "", null);
    }

    /**
     * Get specified data attributes (by keys) of a workflow
     *
     * @param workflowClass     required
     * @param workflowId        required
     * @param workflowRunId     optional, can be empty
     * @param keys              required, cannot be empty or null
     * @return the data attributes
     */
    public Map<String, Object> getWorkflowDataAttributes(
            final Class<? extends ObjectWorkflow> workflowClass,
            final String workflowId,
            final String workflowRunId,
            final List<String> keys) {

        if (keys == null || keys.isEmpty()) {
            throw new IllegalArgumentException("keys must contain at least one entry, or use getAllDataAttributes API to get all");
        }
        return doGetWorkflowDataAttributes(workflowClass, workflowId, workflowRunId, keys);
    }

    /**
     * Get specified data attributes (by keys) of a workflow
     *
     * @param workflowClass     required
     * @param workflowId        required
     * @param keys              required, cannot be empty or null
     * @return the data attributes
     */
    public Map<String, Object> getWorkflowDataAttributes(
            final Class<? extends ObjectWorkflow> workflowClass,
            final String workflowId,
            final List<String> keys) {
        return getWorkflowDataAttributes(workflowClass, workflowId, "", keys);
    }

    /**
     * Get all the data attributes of a workflow
     *
     * @param workflowClass     required
     * @param workflowId        required
     * @param workflowRunId     optional, can be empty
     * @return the data attributes
     */
    public Map<String, Object> getAllDataAttributes(
            final Class<? extends ObjectWorkflow> workflowClass,
            final String workflowId,
            final String workflowRunId) {
        return doGetWorkflowDataAttributes(workflowClass, workflowId, workflowRunId, null);
    }

    /**
     * Get all the data attributes of a workflow
     *
     * @param workflowClass     required
     * @param workflowId        required
     * @return the data attributes
     */
    public Map<String, Object> getAllDataAttributes(
            final Class<? extends ObjectWorkflow> workflowClass,
            final String workflowId) {
        return getAllDataAttributes(workflowClass, workflowId, "");
    }

    private Map<String, Object> doGetWorkflowDataAttributes(
            final Class<? extends ObjectWorkflow> workflowClass,
            final String workflowId,
            final String workflowRunId,
            final List<String> keys) {
        final String wfType = workflowClass.getSimpleName();
        checkWorkflowTypeExists(wfType);

        final TypeStore dataAttributeTypeStore = registry.getDataAttributeTypeStore(wfType);

        // if attribute keys is null or empty, iwf server will return all data attributes
        if (keys != null && !keys.isEmpty()) {
            final Optional<String> first = keys.stream()
                    .filter(key -> !dataAttributeTypeStore.isValidNameOrPrefix(key))
                    .findFirst();
            if (first.isPresent()) {
                throw new IllegalArgumentException(
                        String.format(
                                "data attribute not registered: %s",
                                first.get()
                        )
                );
            }
        }

        final PersistenceOptions schemaOptions = registry.getPersistenceOptions(wfType);

        final WorkflowGetDataObjectsResponse response = unregisteredClient.getAnyWorkflowDataObjects(workflowId, workflowRunId, keys, schemaOptions.getEnableCaching());

        if (response.getObjects() == null) {
            throw new IllegalStateException("data attributes not returned");
        }

        final Map<String, Object> result = new HashMap<>();
        for (final KeyValue keyValue : response.getObjects()) {
            if (keyValue.getValue() != null) {
                result.put(
                        keyValue.getKey(),
                        clientOptions.getObjectEncoder().decode(
                                keyValue.getValue(),
                                dataAttributeTypeStore.getType(keyValue.getKey())
                        )
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

    /**
     * create a new stub for invoking RPC
     *
     * @param workflowClassForRpc the class of defining the RPCs to invoke
     * @param workflowId          required
     * @param workflowRunId       optional, can be empty
     * @param <T>                 the class of defining the RPCs to invoke
     * @return the result of the RPC
     */
    public <T> T newRpcStub(Class<T> workflowClassForRpc, String workflowId, String workflowRunId) {

        final String wfType = workflowClassForRpc.getSimpleName();
        final PersistenceOptions schemaOptions = registry.getPersistenceOptions(wfType);
        final Map<String, SearchAttributeValueType> searchAttributeKeyToTypeMap = registry.getSearchAttributeKeyToTypeMap(wfType);
        List<SearchAttributeKeyAndType> keyAndTypes = new ArrayList<>();

        searchAttributeKeyToTypeMap.forEach((key, type) -> {
            final SearchAttributeKeyAndType keyAndType = new SearchAttributeKeyAndType()
                    .key(key)
                    .valueType(type);
            keyAndTypes.add(keyAndType);
        });

        Class<?> dynamicType = new ByteBuddy()
                .subclass(workflowClassForRpc)
                .method(ElementMatchers.any())
                .intercept(MethodDelegation.to(new RpcInvocationHandler(this.unregisteredClient, workflowId, workflowRunId, schemaOptions, keyAndTypes)))
                .make()
                .load(getClass().getClassLoader())
                .getLoaded();

        T result;
        try {
            if(dynamicType.getConstructors().length ==0){
                throw new WorkflowDefinitionException("workflow must define at least a constructor");
            }
            final Constructor<?> constructor = dynamicType.getConstructors()[0];
            final int parameterCount = constructor.getParameterCount();
            final Object[] params = new Object[parameterCount];

            result = (T) constructor.newInstance(params);
        } catch (InvocationTargetException | InstantiationException | IllegalAccessException e) {
            throw new IllegalStateException(e);
        }

        return result;
    }

    /**
     * create a new stub for invoking RPC
     *
     * @param workflowClassForRpc the class of defining the RPCs to invoke
     * @param workflowId          required
     * @param <T>                 the class of defining the RPCs to invoke
     * @return the result of the RPC
     */
    public <T> T newRpcStub(Class<T> workflowClassForRpc, String workflowId) {
        return newRpcStub(workflowClassForRpc, workflowId, "");
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

    /**
     * Get specified search attributes (by attributeKeys) of a workflow
     *
     * @param workflowClass     required
     * @param workflowId        required
     * @param workflowRunId     optional, can be empty
     * @param attributeKeys     required, cannot be empty or null
     * @return the search attributes
     */
    public Map<String, Object> getWorkflowSearchAttributes(
            final Class<? extends ObjectWorkflow> workflowClass,
            final String workflowId,
            final String workflowRunId,
            List<String> attributeKeys) {
        if (attributeKeys == null || attributeKeys.isEmpty()) {
            throw new IllegalArgumentException("attributeKeys must contain at least one entry, or use GetAllSearchAttributes API to get all");
        }
        return doGetWorkflowSearchAttributes(workflowClass, workflowId, workflowRunId, attributeKeys);
    }

    /**
     * Get specified search attributes (by attributeKeys) of a workflow
     *
     * @param workflowClass     required
     * @param workflowId        required
     * @param attributeKeys     required, cannot be empty or null
     * @return the search attributes
     */
    public Map<String, Object> getWorkflowSearchAttributes(
            final Class<? extends ObjectWorkflow> workflowClass,
            final String workflowId,
            List<String> attributeKeys) {
        return getWorkflowSearchAttributes(workflowClass, workflowId, "", attributeKeys);
    }

    /**
     * Get all the search attributes of a workflow
     *
     * @param workflowClass     required
     * @param workflowId        required
     * @param workflowRunId     optional, can be empty
     * @return the search attributes
     */
    public Map<String, Object> getAllSearchAttributes(
            final Class<? extends ObjectWorkflow> workflowClass,
            final String workflowId,
            final String workflowRunId) {
        return doGetWorkflowSearchAttributes(workflowClass, workflowId, workflowRunId, null);
    }

    /**
     * Get all the search attributes of a workflow
     *
     * @param workflowClass     required
     * @param workflowId        required
     * @return the search attributes
     */
    public Map<String, Object> getAllSearchAttributes(
            final Class<? extends ObjectWorkflow> workflowClass,
            final String workflowId) {
        return getAllSearchAttributes(workflowClass, workflowId, "");
    }

    /**
     * Describe a workflow to get its info.
     * If the workflow does not exist, throw the WORKFLOW_NOT_EXISTS_SUB_STATUS exception.
     *
     * @param workflowId    required
     * @param workflowRunId optional, can be empty
     * @return the workflow's info
     */
    public WorkflowInfo describeWorkflow(
            final String workflowId,
            final String workflowRunId) {
        final WorkflowGetResponse response = unregisteredClient.getWorkflow(workflowId, workflowRunId, false);
        return WorkflowInfo.builder()
                .workflowStatus(response.getWorkflowStatus())
                .build();
    }

    /**
     * Describe a workflow to get its info.
     * If the workflow does not exist, throw the WORKFLOW_NOT_EXISTS_SUB_STATUS exception.
     *
     * @param workflowId    required
     * @return the workflow's info
     */
    public WorkflowInfo describeWorkflow(
            final String workflowId) {
        return describeWorkflow(workflowId, "");
    }

    private Map<String, Object> doGetWorkflowSearchAttributes(
            final Class<? extends ObjectWorkflow> workflowClass,
            final String workflowId,
            final String workflowRunId,
            final List<String> attributeKeys) {
        final String wfType = workflowClass.getSimpleName();
        checkWorkflowTypeExists(wfType);

        final Map<String, SearchAttributeValueType> searchAttributeKeyToTypeMap = registry.getSearchAttributeKeyToTypeMap(wfType);

        // if attribute keys is null or empty, iwf server will return all search attributes
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
