package io.iworkflow.core;

import io.iworkflow.core.communication.InternalChannelDef;
import io.iworkflow.core.communication.SignalChannelDef;
import io.iworkflow.core.persistence.DataAttributeDef;
import io.iworkflow.core.persistence.PersistenceFieldDef;
import io.iworkflow.core.persistence.PersistenceOptions;
import io.iworkflow.core.persistence.SearchAttributeDef;
import io.iworkflow.gen.models.SearchAttributeValueType;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

public class Registry {
    private final Map<String, ObjectWorkflow> workflowStore = new HashMap<>();
    // (workflow type, stateId)-> StateDef
    private final Map<String, StateDef> workflowStateStore = new HashMap<>(); // TODO refactor to use Map<String, Map<String, StateDef>> to be more clear

    private final Map<String, StateDef> workflowStartStateStore = new HashMap<>();
    private final Map<String, Map<String, Class<?>>> signalTypeStore = new HashMap<>();

    private final Map<String, Map<String, Class<?>>> internalChannelTypeStore = new HashMap<>();
    private final Map<String, Map<String, Class<?>>> dataAttributeTypeStore = new HashMap<>();

    private final Map<String, Map<String, SearchAttributeValueType>> searchAttributeTypeStore = new HashMap<>();

    private final Map<String, PersistenceOptions> persistenceOptionsMap = new HashMap<>();
    private final Map<String, Map<String, Method>> rpcMethodStore = new HashMap<>();

    private static final String DELIMITER = "_";

    /**
     * Note:
     * This constructor is going to be deprecated.
     * It's now recommended to use the {@link #Registry(Client client)} constructor to create a registry
     * and initialize the client's {@link Client#registry} field.
     */
    @Deprecated
    public Registry() {
    }

    /**
     * Initialize the {@link Client#registry} field of a client created by {@link Client#partialBuilder(ClientOptions)}.
     * Otherwise, public API calls of the client will throw the {@link ClientNotFullyInitializedException}.
     *
     * @param client required
     */
    public Registry(final Client client) {
        client.initializeRegistry(this);
    }

    public void addWorkflows(final ObjectWorkflow... wfs) {
        Arrays.stream(wfs).forEach(this::addWorkflow);
    }

    public void addWorkflows(final List<ObjectWorkflow> wfs) {
        wfs.forEach(this::addWorkflow);
    }

    public void addWorkflow(final ObjectWorkflow wf) {
        registerWorkflow(wf);
        registerWorkflowState(wf);
        registerWorkflowSignal(wf);
        registerWorkflowInternalChannel(wf);
        registerWorkflowDataAttributes(wf);
        registerWorkflowSearchAttributes(wf);
        registerPersistenceOptions(wf);
        registerWorkflowRPCs(wf);
    }
    public static String getWorkflowType(final ObjectWorkflow wf) {
        if (wf.getWorkflowType().isEmpty()) {
            return wf.getClass().getSimpleName();
        }
        return wf.getWorkflowType();
    }

    private void registerWorkflow(final ObjectWorkflow wf) {
        String workflowType = getWorkflowType(wf);

        if (workflowStore.containsKey(workflowType)) {
            throw new WorkflowDefinitionException(String.format("Workflow type %s already exists", workflowType));
        }
        workflowStore.put(workflowType, wf);
    }
    private void registerWorkflowState(final ObjectWorkflow wf) {
        String workflowType = getWorkflowType(wf);
        int startingStates = 0;
        StateDef startState = null;
        for (StateDef stateDef : wf.getWorkflowStates()) {
            String key = getStateDefKey(workflowType, stateDef.getWorkflowState().getStateId());
            if (workflowStateStore.containsKey(key)) {
                throw new WorkflowDefinitionException(String.format("Workflow state definition %s already exists", key));
            } else {
                workflowStateStore.put(key, stateDef);
                if (stateDef.getCanStartWorkflow()) {
                    startingStates++;
                    startState = stateDef;
                }
            }
        }
        if (startingStates > 1) {
            throw new WorkflowDefinitionException(String.format("Workflow cannot have more than one starting states, found %d", startingStates));
        }
        workflowStartStateStore.put(workflowType, startState);
    }

    private void registerWorkflowRPCs(final ObjectWorkflow wf) {
        String workflowType = getWorkflowType(wf);
        final Method[] methods = wf.getClass().getDeclaredMethods();
        if (methods.length == 0) {
            rpcMethodStore.put(workflowType, new HashMap<>());
            return;
        }

        Arrays.stream(methods).forEach(method -> {
            final RPC rpcAnno = method.getAnnotation(RPC.class);
            if (rpcAnno == null) {
                // skip methods that are not RPC
                return;
            }
            RpcDefinitions.validateRpcMethod(method);
            Map<String, Method> rpcMap =
                    rpcMethodStore.computeIfAbsent(workflowType, s -> new HashMap<>());
            rpcMap.put(method.getName(), method);
        });
    }

    private void registerWorkflowSignal(final ObjectWorkflow wf) {
        String workflowType = getWorkflowType(wf);
        final List<SignalChannelDef> channels = getSignalChannels(wf);
        if (channels == null || channels.isEmpty()) {
            signalTypeStore.put(workflowType, new HashMap<>());
            return;
        }

        for (SignalChannelDef signalChannelDef : channels) {
            Map<String, Class<?>> signalNameToTypeMap =
                    signalTypeStore.computeIfAbsent(workflowType, s -> new HashMap<>());
            if (signalNameToTypeMap.containsKey(signalChannelDef.getSignalChannelName())) {
                throw new WorkflowDefinitionException(
                        String.format("Signal channel name  %s already exists", signalChannelDef.getSignalChannelName()));
            }
            signalNameToTypeMap.put(signalChannelDef.getSignalChannelName(), signalChannelDef.getSignalValueType());
        }
    }

    private void registerWorkflowInternalChannel(final ObjectWorkflow wf) {
        String workflowType = getWorkflowType(wf);
        final List<InternalChannelDef> channels = getInternalChannels(wf);
        if (channels == null || channels.isEmpty()) {
            internalChannelTypeStore.put(workflowType, new HashMap<>());
            return;
        }

        for (InternalChannelDef internalChannelDef : channels) {
            Map<String, Class<?>> nameToTypeMap =
                    internalChannelTypeStore.computeIfAbsent(workflowType, s -> new HashMap<>());
            if (nameToTypeMap.containsKey(internalChannelDef.getChannelName())) {
                throw new WorkflowDefinitionException(
                        String.format("InternalChannel name  %s already exists", internalChannelDef.getChannelName()));
            }
            nameToTypeMap.put(internalChannelDef.getChannelName(), internalChannelDef.getValueType());
        }
    }

    private void registerWorkflowDataAttributes(final ObjectWorkflow wf) {
        String workflowType = getWorkflowType(wf);
        final List<DataAttributeDef> fields = getDataAttributeFields(wf);
        if (fields == null || fields.isEmpty()) {
            dataAttributeTypeStore.put(workflowType, new HashMap<>());
            return;
        }

        for (DataAttributeDef dataAttributeField : fields) {
            Map<String, Class<?>> dataAttributeKeyToTypeMap =
                    dataAttributeTypeStore.computeIfAbsent(workflowType, s -> new HashMap<>());
            if (dataAttributeKeyToTypeMap.containsKey(dataAttributeField.getKey())) {
                throw new WorkflowDefinitionException(
                        String.format(
                                "data attribute key %s already exists",
                                dataAttributeField.getDataAttributeType())
                );
            }
            dataAttributeKeyToTypeMap.put(
                    dataAttributeField.getKey(),
                    dataAttributeField.getDataAttributeType()
            );
        }
    }

    private void registerPersistenceOptions(final ObjectWorkflow wf) {
        String workflowType = getWorkflowType(wf);
        this.persistenceOptionsMap.put(workflowType, wf.getPersistenceOptions());
    }

    private List<DataAttributeDef> getDataAttributeFields(final ObjectWorkflow wf) {
        final Set<String> keySet = wf.getPersistenceSchema().stream().map(PersistenceFieldDef::getKey).collect(Collectors.toSet());
        if (keySet.size() != wf.getPersistenceSchema().size()) {
            throw new WorkflowDefinitionException("cannot have conflict key definition in persistence schema");
        }
        return wf.getPersistenceSchema().stream().filter((f) -> f instanceof DataAttributeDef).map(f -> (DataAttributeDef) f).collect(Collectors.toList());
    }

    private List<SearchAttributeDef> getSearchAttributeFields(final ObjectWorkflow wf) {
        final Set<String> keySet = wf.getPersistenceSchema().stream().map(PersistenceFieldDef::getKey).collect(Collectors.toSet());
        if (keySet.size() != wf.getPersistenceSchema().size()) {
            throw new WorkflowDefinitionException("cannot have conflict key definition in persistence schema");
        }
        return wf.getPersistenceSchema().stream().filter((f) -> f instanceof SearchAttributeDef).map(f -> (SearchAttributeDef) f).collect(Collectors.toList());
    }

    private List<InternalChannelDef> getInternalChannels(final ObjectWorkflow wf) {
        return wf.getCommunicationSchema().stream().filter((f) -> f instanceof InternalChannelDef).map(f -> (InternalChannelDef) f).collect(Collectors.toList());
    }

    private List<SignalChannelDef> getSignalChannels(final ObjectWorkflow wf) {
        return wf.getCommunicationSchema().stream().filter((f) -> f instanceof SignalChannelDef).map(f -> (SignalChannelDef) f).collect(Collectors.toList());
    }

    private void registerWorkflowSearchAttributes(final ObjectWorkflow wf) {
        String workflowType = getWorkflowType(wf);
        final List<SearchAttributeDef> fields = getSearchAttributeFields(wf);
        if (fields == null || fields.isEmpty()) {
            searchAttributeTypeStore.put(workflowType, new HashMap<>());
            return;
        }

        for (SearchAttributeDef searchAttributeField : fields) {
            Map<String, SearchAttributeValueType> searchAttributeKeyToTypeMap =
                    searchAttributeTypeStore.computeIfAbsent(workflowType, s -> new HashMap<>());

            if (searchAttributeKeyToTypeMap.containsKey(searchAttributeField.getKey())) {
                throw new WorkflowDefinitionException(
                        String.format(
                                "Search attribute key %s already exists",
                                searchAttributeField.getKey())
                );
            }
            searchAttributeKeyToTypeMap.put(
                    searchAttributeField.getKey(),
                    searchAttributeField.getSearchAttributeType()
            );
        }
    }

    public ObjectWorkflow getWorkflow(final String workflowType) {
        return workflowStore.get(workflowType);
    }

    public Method getWorkflowRpcMethod(final String workflowType, final String rpcName) {
        final Map<String, Method> wfRRPCs = rpcMethodStore.get(workflowType);
        if (wfRRPCs == null) {
            throw new WorkflowDefinitionException(String.format("workflow type %s is not registered, all registered types are: %s", workflowType, workflowStore.keySet()));
        }
        return wfRRPCs.get(rpcName);
    }

    public StateDef getWorkflowState(final String workflowType, final String stateId) {
        return workflowStateStore.get(getStateDefKey(workflowType, stateId));
    }

    public Optional<StateDef> getWorkflowStartingState(final String workflowType) {
        final StateDef state = workflowStartStateStore.get(workflowType);
        return Optional.ofNullable(state);
    }

    public Map<String, Class<?>> getSignalChannelNameToSignalTypeMap(final String workflowType) {
        return signalTypeStore.get(workflowType);
    }

    public Map<String, Class<?>> getInternalChannelNameToTypeMap(final String workflowType) {
        return internalChannelTypeStore.get(workflowType);
    }

    public Map<String, Class<?>> getDataAttributeKeyToTypeMap(final String workflowType) {
        return dataAttributeTypeStore.get(workflowType);
    }

    public Map<String, SearchAttributeValueType> getSearchAttributeKeyToTypeMap(final String workflowType) {
        return searchAttributeTypeStore.get(workflowType);
    }

    public PersistenceOptions getPersistenceOptions(final String workflowType) {
        return persistenceOptionsMap.get(workflowType);
    }

    private String getStateDefKey(final String workflowType, final String stateId) {
        return workflowType + DELIMITER + stateId;
    }
}
