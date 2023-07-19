package io.iworkflow.core;

import io.iworkflow.core.command.CommandRequest;
import io.iworkflow.core.communication.CommunicationImpl;
import io.iworkflow.core.communication.InternalChannelCommand;
import io.iworkflow.core.mapper.CommandRequestMapper;
import io.iworkflow.core.mapper.CommandResultsMapper;
import io.iworkflow.core.mapper.StateDecisionMapper;
import io.iworkflow.core.persistence.DataAttributesRWImpl;
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

import static io.iworkflow.core.RpcDefinitions.INDEX_OF_INPUT_PARAMETER;
import static io.iworkflow.core.RpcDefinitions.PARAMETERS_WITH_INPUT;

public class WorkerService {

    public static final String WORKFLOW_STATE_WAIT_UNTIL_API_PATH = "/api/v1/workflowState/start";
    public static final String WORKFLOW_STATE_EXECUTE_API_PATH = "/api/v1/workflowState/decide";

    public static final String WORKFLOW_WORKER_RPC_API_PATH = "/api/v1/workflowWorker/rpc";

    private final Registry registry;

    private final WorkerOptions workerOptions;

    public WorkerService(Registry registry, WorkerOptions workerOptions) {
        this.registry = registry;
        this.workerOptions = workerOptions;
    }

    public WorkflowWorkerRpcResponse handleWorkflowWorkerRpc(final WorkflowWorkerRpcRequest req) {
        final ObjectWorkflow workflow = registry.getWorkflow(req.getWorkflowType());
        final Method method = registry.getWorkflowRpcMethod(req.getWorkflowType(), req.getRpcName());
        Object input = null;
        if (method.getParameterTypes().length == PARAMETERS_WITH_INPUT) {
            // the second one will be input
            Class<?> inputType = method.getParameterTypes()[INDEX_OF_INPUT_PARAMETER];
            input = workerOptions.getObjectEncoder().decode(req.getInput(), inputType);
        }

        final DataAttributesRWImpl dataObjectsRW =
                createDataObjectsRW(req.getWorkflowType(), req.getDataAttributes());
        final Context context = fromIdlContext(req.getContext());

        final Map<String, SearchAttributeValueType> searchAttrsTypeMap = registry.getSearchAttributeKeyToTypeMap(req.getWorkflowType());
        final SearchAttributeRWImpl searchAttributeRW = new SearchAttributeRWImpl(searchAttrsTypeMap, req.getSearchAttributes());
        final CommunicationImpl communication = new CommunicationImpl(
                registry.getInternalChannelNameToTypeMap(req.getWorkflowType()), workerOptions.getObjectEncoder(), true);

        final StateExecutionLocalsImpl stateExeLocals = new StateExecutionLocalsImpl(toMap(null), workerOptions.getObjectEncoder());
        Persistence persistence = new PersistenceImpl(dataObjectsRW, searchAttributeRW, stateExeLocals);

        Object output = null;
        try {
            if (method.getParameterTypes().length == PARAMETERS_WITH_INPUT) {
                output = method.invoke(
                        workflow,
                        context,
                        input,
                        persistence,
                        communication);
            } else {
                // without input
                output = method.invoke(
                        workflow,
                        context,
                        persistence,
                        communication);
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
        StateDef state = registry.getWorkflowState(req.getWorkflowType(), req.getWorkflowStateId());
        final EncodedObject stateInput = req.getStateInput();
        final Object input = workerOptions.getObjectEncoder().decode(stateInput, state.getWorkflowState().getInputType());
        final DataAttributesRWImpl dataObjectsRW =
                createDataObjectsRW(req.getWorkflowType(), req.getDataObjects());
        final Context context = fromIdlContext(req.getContext());

        final Map<String, SearchAttributeValueType> searchAttrsTypeMap = registry.getSearchAttributeKeyToTypeMap(req.getWorkflowType());
        final SearchAttributeRWImpl searchAttributeRW = new SearchAttributeRWImpl(searchAttrsTypeMap, req.getSearchAttributes());
        final CommunicationImpl communication = new CommunicationImpl(
                registry.getInternalChannelNameToTypeMap(req.getWorkflowType()), workerOptions.getObjectEncoder(), false);

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
        StateDef state = registry.getWorkflowState(req.getWorkflowType(), req.getWorkflowStateId());
        final Object input;
        final EncodedObject stateInput = req.getStateInput();
        input = workerOptions.getObjectEncoder().decode(stateInput, state.getWorkflowState().getInputType());
        final DataAttributesRWImpl dataObjectsRW =
                createDataObjectsRW(req.getWorkflowType(), req.getDataObjects());

        final Context context = fromIdlContext(req.getContext());
        final StateExecutionLocalsImpl stateExeLocals = new StateExecutionLocalsImpl(toMap(req.getStateLocals()), workerOptions.getObjectEncoder());
        final Map<String, SearchAttributeValueType> saTypeMap = registry.getSearchAttributeKeyToTypeMap(req.getWorkflowType());
        final SearchAttributeRWImpl searchAttributeRW = new SearchAttributeRWImpl(saTypeMap, req.getSearchAttributes());
        final CommunicationImpl communication = new CommunicationImpl(
                registry.getInternalChannelNameToTypeMap(req.getWorkflowType()), workerOptions.getObjectEncoder(), false);

        Persistence persistence = new PersistenceImpl(dataObjectsRW, searchAttributeRW, stateExeLocals);

        StateDecision stateDecision = state.getWorkflowState().execute(
                context,
                input,
                CommandResultsMapper.fromGenerated(
                        req.getCommandResults(),
                        registry.getSignalChannelNameToSignalTypeMap(req.getWorkflowType()),
                        registry.getInternalChannelNameToTypeMap(req.getWorkflowType()),
                        workerOptions.getObjectEncoder()),
                persistence,
                communication);

        final WorkflowStateExecuteResponse response = new WorkflowStateExecuteResponse()
                .stateDecision(StateDecisionMapper.toGenerated(stateDecision, req.getWorkflowType(), registry, workerOptions.getObjectEncoder()));

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
                registry.getDataAttributeKeyToTypeMap(workflowType),
                registry.getDataAttributePrefixToTypeMap(workflowType),
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

    private Context fromIdlContext(final io.iworkflow.gen.models.Context context) {
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
                .workflowRunId(context.getWorkflowRunId())
                .workflowStartTimestampSeconds(context.getWorkflowStartedTimestamp())
                .stateExecutionId(Optional.ofNullable(context.getStateExecutionId()))
                .attempt(attempt)
                .firstAttemptTimestampSeconds(firstAttemptTimestamp)
                .build();
    }
}

