package io.iworkflow.core;

import io.iworkflow.core.command.CommandRequest;
import io.iworkflow.core.communication.CommunicationImpl;
import io.iworkflow.core.communication.InternalChannelCommand;
import io.iworkflow.core.mapper.CommandRequestMapper;
import io.iworkflow.core.mapper.CommandResultsMapper;
import io.iworkflow.core.mapper.StateDecisionMapper;
import io.iworkflow.core.db.DbSyncStatePayload;
import io.iworkflow.core.db.PostgresDataAttributeSyncer;
import io.iworkflow.core.persistence.CellLocation;
import io.iworkflow.core.persistence.DataAttributesRWImpl;
import io.iworkflow.core.persistence.DbAttributeSync;
import io.iworkflow.core.persistence.Persistence;
import io.iworkflow.core.persistence.PersistenceImpl;
import io.iworkflow.core.persistence.SearchAttributeRWImpl;
import io.iworkflow.core.persistence.StateExecutionLocalsImpl;
import io.iworkflow.gen.models.EncodedObject;
import io.iworkflow.gen.models.InterStateChannelPublishing;
import io.iworkflow.gen.models.KeyValue;
import io.iworkflow.gen.models.SearchAttribute;
import io.iworkflow.gen.models.SearchAttributeValueType;
import io.iworkflow.gen.models.WorkflowStateExecuteRequest;
import io.iworkflow.gen.models.WorkflowStateExecuteResponse;
import io.iworkflow.gen.models.WorkflowStateWaitUntilRequest;
import io.iworkflow.gen.models.WorkflowStateWaitUntilResponse;
import io.iworkflow.gen.models.WorkflowWorkerRpcRequest;
import io.iworkflow.gen.models.WorkflowWorkerRpcResponse;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

public class WorkerService {

    public static final String WORKFLOW_STATE_WAIT_UNTIL_API_PATH = "/api/v1/workflowState/start";
    public static final String WORKFLOW_STATE_EXECUTE_API_PATH = "/api/v1/workflowState/decide";

    public static final String WORKFLOW_WORKER_RPC_API_PATH = "/api/v1/workflowWorker/rpc";

    // Reserved stateIds for the data-attribute DB-sync system states. These are handled specially in
    // this worker and are NOT registered in the Registry. They deliberately do not use the server-
    // interpreted "_SYS_" prefix, and movements targeting them are always built directly in generated
    // form (never routed through StateMovementMapper), so they need no registry validation.
    public static final String LOAD_DATA_ATTRIBUTES_FROM_DB_STATE_ID = "_iwf_LoadDataAttributesFromDbState";
    public static final String SYNC_DATA_ATTRIBUTES_TO_DB_STATE_ID = "_iwf_SyncDataAttributesToDbState";

    private final Registry registry;

    private final WorkerOptions workerOptions;

    public WorkerService(Registry registry, WorkerOptions workerOptions) {
        this.registry = registry;
        this.workerOptions = workerOptions;
    }

    public WorkflowWorkerRpcResponse handleWorkflowWorkerRpc(final WorkflowWorkerRpcRequest req) {
        final ObjectWorkflow workflow = registry.getWorkflow(req.getWorkflowType());
        final Method method = registry.getWorkflowRpcMethod(req.getWorkflowType(), req.getRpcName());

        RpcMethodMetadata methodMetadata = RpcMethodMatcher.match(method);
        if (methodMetadata == null) {
            throw new WorkflowDefinitionException("An RPC method must be annotated by RPC annotation and matches one of the RPC definitions");
        }
        Object input = null;
        if (methodMetadata.hasInput()) {
            // the second one will be input
            Class<?> inputType = method.getParameterTypes()[methodMetadata.getInputIndex()];
            input = workerOptions.getObjectEncoder().decode(req.getInput(), inputType);
        }

        final DataAttributesRWImpl dataObjectsRW =
                createDataObjectsRW(req.getWorkflowType(), req.getDataAttributes());
        final Context context = fromIdlContext(req.getContext(), req.getWorkflowType());

        final Map<String, SearchAttributeValueType> searchAttrsTypeMap = registry.getSearchAttributeKeyToTypeMap(req.getWorkflowType());
        final SearchAttributeRWImpl searchAttributeRW = new SearchAttributeRWImpl(searchAttrsTypeMap, req.getSearchAttributes());
        final CommunicationImpl communication = new CommunicationImpl(
                req.getInternalChannelInfos(),
                req.getSignalChannelInfos(),
                registry.getInternalChannelTypeStore(req.getWorkflowType()),
                registry.getSignalChannelTypeStore(req.getWorkflowType()),
                workerOptions.getObjectEncoder(),
                true
        );

        final StateExecutionLocalsImpl stateExeLocals = new StateExecutionLocalsImpl(toMap(null), workerOptions.getObjectEncoder());
        Persistence persistence = new PersistenceImpl(dataObjectsRW, searchAttributeRW, stateExeLocals);

        Object output = null;
        try {
            if (methodMetadata.usesPersistence()) {
                if (methodMetadata.hasInput()) {
                    output = method.invoke(
                            workflow,
                            context,
                            input,
                            persistence,
                            communication
                    );
                } else {
                    output = method.invoke(
                            workflow,
                            context,
                            persistence,
                            communication
                    );
                }
            } else {
                if (methodMetadata.hasInput()) {
                    output = method.invoke(
                            workflow,
                            context,
                            input,
                            communication
                    );
                } else {
                    output = method.invoke(
                            workflow,
                            context,
                            communication
                    );
                }
            }
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        } catch (InvocationTargetException e) {
            if (e.getTargetException() instanceof RuntimeException) {
                throw (RuntimeException) e.getTargetException();
            }
            throw new RuntimeException(e.getTargetException());
        }

        final EncodedObject encodedOutput = this.workerOptions.getObjectEncoder().encode(output);
        final WorkflowWorkerRpcResponse response = new WorkflowWorkerRpcResponse()
                .output(encodedOutput);

        if (dataObjectsRW.getToReturnToServer().size() > 0) {
            response.upsertDataAttributes(dataObjectsRW.getToReturnToServer());
        }

        if (stateExeLocals.getRecordEvents().size() > 0) {
            response.recordEvents(stateExeLocals.getRecordEvents());
        }

        if (communication.getStateMovements().size() > 0) {
            final StateDecision stateDecision = StateDecision.multiNextStates(communication.getStateMovements());
            response.stateDecision(
                    StateDecisionMapper.toGenerated(stateDecision, req.getWorkflowType(), registry, workerOptions.getObjectEncoder())
            );
        }
        final List<SearchAttribute> upsertSAs = createUpsertSearchAttributes(
                searchAttrsTypeMap,
                searchAttributeRW.getUpsertToServerInt64AttributeMap(),
                searchAttributeRW.getUpsertToServerStringAttributeMap(),
                searchAttributeRW.getUpsertToServerBooleanAttributeMap(),
                searchAttributeRW.getUpsertToServerDoubleAttributeMap(),
                searchAttributeRW.getUpsertToServerStringArrayAttributeMap()
        );
        if (upsertSAs.size() > 0) {
            response.upsertSearchAttributes(upsertSAs);
        }
        final List<InterStateChannelPublishing> interStateChannelPublishing = toInterStateChannelPublishing(communication.getToPublishInternalChannels());
        if (interStateChannelPublishing.size() > 0) {
            response.publishToInterStateChannel(interStateChannelPublishing);
        }
        return response;
    }

    public WorkflowStateWaitUntilResponse handleWorkflowStateWaitUntil(final WorkflowStateWaitUntilRequest req) {
        // The DB-sync system states skip waitUntil (they set skipWaitUntil=true). This is a defensive
        // guard: they are not registered, so the registry lookup below would otherwise throw.
        if (LOAD_DATA_ATTRIBUTES_FROM_DB_STATE_ID.equals(req.getWorkflowStateId())
                || SYNC_DATA_ATTRIBUTES_TO_DB_STATE_ID.equals(req.getWorkflowStateId())) {
            return new WorkflowStateWaitUntilResponse()
                    .commandRequest(CommandRequestMapper.toGenerated(CommandRequest.empty));
        }

        StateDef state = registry.getWorkflowState(req.getWorkflowType(), req.getWorkflowStateId());
        final EncodedObject stateInput = req.getStateInput();
        final Object input = workerOptions.getObjectEncoder().decode(stateInput, state.getWorkflowState().getInputType());
        final DataAttributesRWImpl dataObjectsRW =
                createDataObjectsRW(req.getWorkflowType(), req.getDataObjects());
        final Context context = fromIdlContext(req.getContext(), req.getWorkflowType());

        final Map<String, SearchAttributeValueType> searchAttrsTypeMap = registry.getSearchAttributeKeyToTypeMap(req.getWorkflowType());
        final SearchAttributeRWImpl searchAttributeRW = new SearchAttributeRWImpl(searchAttrsTypeMap, req.getSearchAttributes());
        final CommunicationImpl communication = new CommunicationImpl(
                new HashMap<>(),
                new HashMap<>(),
                registry.getInternalChannelTypeStore(req.getWorkflowType()),
                registry.getSignalChannelTypeStore(req.getWorkflowType()),
                workerOptions.getObjectEncoder(),
                false
        );

        final StateExecutionLocalsImpl stateExeLocals = new StateExecutionLocalsImpl(toMap(null), workerOptions.getObjectEncoder());
        Persistence persistence = new PersistenceImpl(dataObjectsRW, searchAttributeRW, stateExeLocals);
        CommandRequest commandRequest = state.getWorkflowState().waitUntil(
                context,
                input,
                persistence,
                communication);

        commandRequest.getCommands().forEach(cmd -> {
            if (cmd instanceof InternalChannelCommand) {
                final String name = ((InternalChannelCommand) cmd).getChannelName();
                if (communication.getToPublishInternalChannels().containsKey(name)) {
                    throw new WorkflowDefinitionException("it's not allowed to publish and wait for the same interstate channel - " + name);
                }
            }
        });

        final WorkflowStateWaitUntilResponse response = new WorkflowStateWaitUntilResponse()
                .commandRequest(CommandRequestMapper.toGenerated(commandRequest));

        if (dataObjectsRW.getToReturnToServer().size() > 0) {
            response.upsertDataObjects(dataObjectsRW.getToReturnToServer());
        }
        if (stateExeLocals.getUpsertStateExecutionLocalAttributes().size() > 0) {
            response.upsertStateLocals(stateExeLocals.getUpsertStateExecutionLocalAttributes());
        }
        if (stateExeLocals.getRecordEvents().size() > 0) {
            response.recordEvents(stateExeLocals.getRecordEvents());
        }
        final List<SearchAttribute> upsertSAs = createUpsertSearchAttributes(
                searchAttrsTypeMap,
                searchAttributeRW.getUpsertToServerInt64AttributeMap(),
                searchAttributeRW.getUpsertToServerStringAttributeMap(),
                searchAttributeRW.getUpsertToServerBooleanAttributeMap(),
                searchAttributeRW.getUpsertToServerDoubleAttributeMap(),
                searchAttributeRW.getUpsertToServerStringArrayAttributeMap()
        );
        if (upsertSAs.size() > 0) {
            response.upsertSearchAttributes(upsertSAs);
        }
        final List<InterStateChannelPublishing> interStateChannelPublishing = toInterStateChannelPublishing(communication.getToPublishInternalChannels());
        if (interStateChannelPublishing.size() > 0) {
            response.publishToInterStateChannel(interStateChannelPublishing);
        }
        return response;
    }

    public WorkflowStateExecuteResponse handleWorkflowStateExecute(final WorkflowStateExecuteRequest req) {
        // The DB-sync system states are handled specially and are not registered in the Registry, so
        // they must be intercepted before the registry lookup below (which would otherwise throw).
        if (LOAD_DATA_ATTRIBUTES_FROM_DB_STATE_ID.equals(req.getWorkflowStateId())) {
            return handleLoadDataAttributesFromDb(req);
        }
        if (SYNC_DATA_ATTRIBUTES_TO_DB_STATE_ID.equals(req.getWorkflowStateId())) {
            return handleSyncDataAttributesToDb(req);
        }

        StateDef state = registry.getWorkflowState(req.getWorkflowType(), req.getWorkflowStateId());
        final Object input;
        final EncodedObject stateInput = req.getStateInput();
        input = workerOptions.getObjectEncoder().decode(stateInput, state.getWorkflowState().getInputType());
        final DataAttributesRWImpl dataObjectsRW =
                createDataObjectsRW(req.getWorkflowType(), req.getDataObjects());

        final Context context = fromIdlContext(req.getContext(), req.getWorkflowType());
        final StateExecutionLocalsImpl stateExeLocals = new StateExecutionLocalsImpl(toMap(req.getStateLocals()), workerOptions.getObjectEncoder());
        final Map<String, SearchAttributeValueType> saTypeMap = registry.getSearchAttributeKeyToTypeMap(req.getWorkflowType());
        final SearchAttributeRWImpl searchAttributeRW = new SearchAttributeRWImpl(saTypeMap, req.getSearchAttributes());
        final CommunicationImpl communication = new CommunicationImpl(
                new HashMap<>(),
                new HashMap<>(),
                registry.getInternalChannelTypeStore(req.getWorkflowType()),
                registry.getSignalChannelTypeStore(req.getWorkflowType()),
                workerOptions.getObjectEncoder(),
                false
        );

        Persistence persistence = new PersistenceImpl(dataObjectsRW, searchAttributeRW, stateExeLocals);

        StateDecision stateDecision = state.getWorkflowState().execute(
                context,
                input,
                CommandResultsMapper.fromGenerated(
                        req.getCommandResults(),
                        registry.getSignalChannelTypeStore(req.getWorkflowType()),
                        registry.getInternalChannelTypeStore(req.getWorkflowType()),
                        workerOptions.getObjectEncoder()),
                persistence,
                communication);

        if (stateDecision == null || stateDecision.getNextStates().isEmpty()) {
            throw new InvalidStateDecisionException("State decision returned by execute method cannot be null or empty");
        }

        final List<KeyValue> toReturnToServer = dataObjectsRW.getToReturnToServer();

        // If this state mutated any DB-synced data attribute, reroute the decision through the sync
        // system state (carrying the captured original decision), so the DB write happens after the
        // iWF server has persisted the mutations. See DbSyncStatePayload.
        final Map<String, DbAttributeSync> dbSyncs = registry.getDbAttributeSyncs(req.getWorkflowType());
        final List<String> mutatedMappedKeys = toReturnToServer.stream()
                .map(KeyValue::getKey)
                .filter(dbSyncs::containsKey)
                .collect(Collectors.toList());

        final io.iworkflow.gen.models.StateDecision generatedDecision;
        if (mutatedMappedKeys.isEmpty()) {
            generatedDecision = StateDecisionMapper.toGenerated(stateDecision, req.getWorkflowType(), registry, workerOptions.getObjectEncoder());
        } else {
            final io.iworkflow.gen.models.StateDecision original =
                    StateDecisionMapper.toGenerated(stateDecision, req.getWorkflowType(), registry, workerOptions.getObjectEncoder());
            final DbSyncStatePayload payload = new DbSyncStatePayload(original, mutatedMappedKeys);
            generatedDecision = new io.iworkflow.gen.models.StateDecision()
                    .addNextStatesItem(new io.iworkflow.gen.models.StateMovement()
                            .stateId(SYNC_DATA_ATTRIBUTES_TO_DB_STATE_ID)
                            .stateInput(workerOptions.getObjectEncoder().encode(payload))
                            .stateOptions(new io.iworkflow.gen.models.WorkflowStateOptions().skipWaitUntil(true)));
        }

        final WorkflowStateExecuteResponse response = new WorkflowStateExecuteResponse()
                .stateDecision(generatedDecision);

        if (toReturnToServer.size() > 0) {
            response.upsertDataObjects(toReturnToServer);
        }
        if (stateExeLocals.getUpsertStateExecutionLocalAttributes().size() > 0) {
            response.upsertStateLocals(stateExeLocals.getUpsertStateExecutionLocalAttributes());
        }
        if (stateExeLocals.getRecordEvents().size() > 0) {
            response.recordEvents(stateExeLocals.getRecordEvents());
        }
        final List<SearchAttribute> upsertSAs = createUpsertSearchAttributes(
                saTypeMap,
                searchAttributeRW.getUpsertToServerInt64AttributeMap(),
                searchAttributeRW.getUpsertToServerStringAttributeMap(),
                searchAttributeRW.getUpsertToServerBooleanAttributeMap(),
                searchAttributeRW.getUpsertToServerDoubleAttributeMap(),
                searchAttributeRW.getUpsertToServerStringArrayAttributeMap()
        );
        if (upsertSAs.size() > 0) {
            response.upsertSearchAttributes(upsertSAs);
        }
        final List<InterStateChannelPublishing> interStateChannelPublishing = toInterStateChannelPublishing(communication.getToPublishInternalChannels());
        if (interStateChannelPublishing.size() > 0) {
            response.publishToInterStateChannel(interStateChannelPublishing);
        }

        return response;
    }

    /**
     * Handles the (unregistered, worker-only) load system state that runs as the workflow's first state
     * when the workflow has DB-synced data attributes. It loads each mapped data attribute from its
     * Postgres cell, then transitions to the real starting state, passing the original start input through.
     */
    private WorkflowStateExecuteResponse handleLoadDataAttributesFromDb(final WorkflowStateExecuteRequest req) {
        final String workflowType = req.getWorkflowType();
        final ObjectEncoder encoder = workerOptions.getObjectEncoder();

        final DataAttributesRWImpl dataObjectsRW = createDataObjectsRW(workflowType, req.getDataObjects());
        final Context context = fromIdlContext(req.getContext(), workflowType);
        final Map<String, SearchAttributeValueType> saTypeMap = registry.getSearchAttributeKeyToTypeMap(workflowType);
        final SearchAttributeRWImpl searchAttributeRW = new SearchAttributeRWImpl(saTypeMap, req.getSearchAttributes());
        final StateExecutionLocalsImpl stateExeLocals = new StateExecutionLocalsImpl(toMap(null), encoder);
        final Persistence persistence = new PersistenceImpl(dataObjectsRW, searchAttributeRW, stateExeLocals);

        final TypeStore typeStore = registry.getDataAttributeTypeStore(workflowType);
        final Map<String, DbAttributeSync> dbSyncs = registry.getDbAttributeSyncs(workflowType);
        for (final Map.Entry<String, DbAttributeSync> entry : dbSyncs.entrySet()) {
            final String key = entry.getKey();
            final DbAttributeSync sync = entry.getValue();
            final CellLocation loc = sync.getLocator().apply(context.getWorkflowId(), persistence);
            final String columnText = PostgresDataAttributeSyncer.select(
                    sync.getDataSource(), sync.getTableName(), sync.getPkColumnName(), loc.getColumnName(), loc.getPkValue());
            if (columnText != null) {
                final Class<?> registeredType = typeStore.getType(key);
                final Object value = encoder.decode(
                        new EncodedObject().encoding(encoder.getEncodingType()).data(columnText), registeredType);
                persistence.setDataAttribute(key, value);
            }
        }

        final Optional<StateDef> startStateOptional = registry.getWorkflowStartingState(workflowType);
        if (!startStateOptional.isPresent()) {
            throw new WorkflowDefinitionException(
                    "the DB-sync load state requires a starting state in workflow " + workflowType);
        }
        final WorkflowState realStartState = startStateOptional.get().getWorkflowState();
        final Object originalInput = encoder.decode(req.getStateInput(), realStartState.getInputType());
        final StateDecision decision = StateDecision.singleNextState(realStartState.getStateId(), originalInput, null);

        final WorkflowStateExecuteResponse response = new WorkflowStateExecuteResponse()
                .stateDecision(StateDecisionMapper.toGenerated(decision, workflowType, registry, encoder));
        final List<KeyValue> toReturnToServer = dataObjectsRW.getToReturnToServer();
        if (toReturnToServer.size() > 0) {
            response.upsertDataObjects(toReturnToServer);
        }
        return response;
    }

    /**
     * Handles the (unregistered, worker-only) sync system state. It writes each carried data attribute's
     * current value to its Postgres cell (the value has already been persisted by the iWF server), then
     * replays the captured original decision verbatim so the workflow proceeds as intended.
     */
    private WorkflowStateExecuteResponse handleSyncDataAttributesToDb(final WorkflowStateExecuteRequest req) {
        final String workflowType = req.getWorkflowType();
        final ObjectEncoder encoder = workerOptions.getObjectEncoder();
        final DbSyncStatePayload payload = encoder.decode(req.getStateInput(), DbSyncStatePayload.class);

        final DataAttributesRWImpl dataObjectsRW = createDataObjectsRW(workflowType, req.getDataObjects());
        final Context context = fromIdlContext(req.getContext(), workflowType);
        final Map<String, SearchAttributeValueType> saTypeMap = registry.getSearchAttributeKeyToTypeMap(workflowType);
        final SearchAttributeRWImpl searchAttributeRW = new SearchAttributeRWImpl(saTypeMap, req.getSearchAttributes());
        final StateExecutionLocalsImpl stateExeLocals = new StateExecutionLocalsImpl(toMap(null), encoder);
        final Persistence persistence = new PersistenceImpl(dataObjectsRW, searchAttributeRW, stateExeLocals);

        final Map<String, EncodedObject> currentValues = toMap(req.getDataObjects());
        final Map<String, DbAttributeSync> dbSyncs = registry.getDbAttributeSyncs(workflowType);
        for (final String key : payload.getDataAttributeKeys()) {
            final DbAttributeSync sync = dbSyncs.get(key);
            if (sync == null) {
                continue;
            }
            final CellLocation loc = sync.getLocator().apply(context.getWorkflowId(), persistence);
            final EncodedObject current = currentValues.get(key);
            final String columnText = current == null ? null : current.getData();
            PostgresDataAttributeSyncer.update(
                    sync.getDataSource(), sync.getTableName(), sync.getPkColumnName(),
                    loc.getColumnName(), loc.getPkValue(), columnText);
        }

        return new WorkflowStateExecuteResponse().stateDecision(payload.getOriginalDecision());
    }

    private List<InterStateChannelPublishing> toInterStateChannelPublishing(final Map<String, List<EncodedObject>> toPublish) {
        List<InterStateChannelPublishing> results = new ArrayList<>();
        toPublish.forEach((cname, list) -> {
            list.forEach(val -> {
                final InterStateChannelPublishing pub = new InterStateChannelPublishing()
                        .channelName(cname)
                        .value(val);
                results.add(pub);

            });
        });
        return results;
    }

    private DataAttributesRWImpl createDataObjectsRW(final String workflowType, final List<KeyValue> keyValues) {
        final Map<String, EncodedObject> map = toMap(keyValues);
        return new DataAttributesRWImpl(
                registry.getDataAttributeTypeStore(workflowType),
                map,
                workerOptions.getObjectEncoder());
    }

    private Map<String, EncodedObject> toMap(final List<KeyValue> keyValues) {
        final Map<String, EncodedObject> map;
        if (keyValues == null || keyValues.isEmpty()) {
            map = new HashMap<>();
        } else {
            map = keyValues.stream()
                    .filter(keyValue -> keyValue.getValue() != null)
                    .collect(Collectors.toMap(KeyValue::getKey, KeyValue::getValue));
        }
        return map;
    }

    private List<SearchAttribute> createUpsertSearchAttributes(
            final Map<String, SearchAttributeValueType> typeMap,
            final Map<String, Long> upsertToServerInt64AttributeMap,
            final Map<String, String> upsertToServerKeywordAttributeMap,
            final Map<String, Boolean> upsertToServerBoolAttributeMap,
            final Map<String, Double> upsertToServerDoubleAttributeMap,
            final Map<String, List<String>> upsertToServerStringArrayAttributeMap
    ) {
        List<SearchAttribute> sas = new ArrayList<>();
        upsertToServerKeywordAttributeMap.forEach((key, sa) -> {
            final SearchAttribute attr = new SearchAttribute()
                    .key(key)
                    .stringValue(sa)
                    .valueType(typeMap.get(key));
            sas.add(attr);
        });

        upsertToServerStringArrayAttributeMap.forEach((key, sa) -> {
            final SearchAttribute attr = new SearchAttribute()
                    .key(key)
                    .stringArrayValue(sa)
                    .valueType(typeMap.get(key));
            sas.add(attr);
        });

        upsertToServerInt64AttributeMap.forEach((key, sa) -> {
            final SearchAttribute attr = new SearchAttribute()
                    .key(key)
                    .integerValue(sa)
                    .valueType(typeMap.get(key));
            sas.add(attr);
        });

        upsertToServerDoubleAttributeMap.forEach((key, sa) -> {
            final SearchAttribute attr = new SearchAttribute()
                    .key(key)
                    .doubleValue(sa)
                    .valueType(typeMap.get(key));
            sas.add(attr);
        });

        upsertToServerBoolAttributeMap.forEach((key, sa) -> {
            final SearchAttribute attr = new SearchAttribute()
                    .key(key)
                    .boolValue(sa)
                    .valueType(typeMap.get(key));
            sas.add(attr);
        });
        return sas;
    }

    private Context fromIdlContext(final io.iworkflow.gen.models.Context context, final String workflowType) {
        int attempt = -1; //unsupported
        if (context.getAttempt() != null) {
            attempt = context.getAttempt();
        }
        long firstAttemptTimestamp = -1; //unsupported
        if (context.getFirstAttemptTimestamp() != null) {
            firstAttemptTimestamp = context.getFirstAttemptTimestamp();
        }

        return ImmutableContext.builder()
                .workflowId(context.getWorkflowId())
                .workflowType(workflowType)
                .workflowRunId(context.getWorkflowRunId())
                .workflowStartTimestampSeconds(context.getWorkflowStartedTimestamp())
                .stateExecutionId(Optional.ofNullable(context.getStateExecutionId()))
                .childWorkflowRequestId(context.getWorkflowRunId()+"-"+context.getStateExecutionId())
                .attempt(attempt)
                .firstAttemptTimestampSeconds(firstAttemptTimestamp)
                .build();
    }
}

