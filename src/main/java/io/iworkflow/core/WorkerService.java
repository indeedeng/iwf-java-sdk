package io.iworkflow.core;

import io.iworkflow.core.command.CommandRequest;
import io.iworkflow.core.communication.CommunicationImpl;
import io.iworkflow.core.communication.InterStateChannelCommand;
import io.iworkflow.core.mapper.CommandRequestMapper;
import io.iworkflow.core.mapper.CommandResultsMapper;
import io.iworkflow.core.mapper.StateDecisionMapper;
import io.iworkflow.core.persistence.DataObjectsRWImpl;
import io.iworkflow.core.persistence.Persistence;
import io.iworkflow.core.persistence.PersistenceImpl;
import io.iworkflow.core.persistence.SearchAttributeRWImpl;
import io.iworkflow.core.persistence.StateLocalsImpl;
import io.iworkflow.gen.models.EncodedObject;
import io.iworkflow.gen.models.InterStateChannelPublishing;
import io.iworkflow.gen.models.KeyValue;
import io.iworkflow.gen.models.SearchAttribute;
import io.iworkflow.gen.models.WorkflowStateDecideRequest;
import io.iworkflow.gen.models.WorkflowStateDecideResponse;
import io.iworkflow.gen.models.WorkflowStateStartRequest;
import io.iworkflow.gen.models.WorkflowStateStartResponse;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class WorkerService {
    private final Registry registry;

    private final WorkerOptions workerOptions;

    public WorkerService(Registry registry, WorkerOptions workerOptions) {
        this.registry = registry;
        this.workerOptions = workerOptions;
    }

    public WorkflowStateStartResponse handleWorkflowStateStart(final WorkflowStateStartRequest req) {
        StateDef state = registry.getWorkflowState(req.getWorkflowType(), req.getWorkflowStateId());
        final EncodedObject stateInput = req.getStateInput();
        final Object input = workerOptions.getObjectEncoder().decode(stateInput, state.getWorkflowState().getInputType());
        final DataObjectsRWImpl dataObjectsRW =
                createQueryAttributesRW(req.getWorkflowType(), req.getDataObjects());
        final Context context = ImmutableContext.builder()
                .workflowId(req.getContext().getWorkflowId())
                .workflowRunId(req.getContext().getWorkflowRunId())
                .workflowStartTimestampSeconds(req.getContext().getWorkflowStartedTimestamp())
                .stateExecutionId(req.getContext().getStateExecutionId())
                .build();
        final StateLocalsImpl stateLocals = new StateLocalsImpl(toMap(null), workerOptions.getObjectEncoder());
        final SearchAttributeRWImpl searchAttributeRW = new SearchAttributeRWImpl(
                registry.getSearchAttributeKeyToTypeMap(req.getWorkflowType()), req.getSearchAttributes());
        final CommunicationImpl communication = new CommunicationImpl(
                registry.getInterStateChannelNameToTypeMap(req.getWorkflowType()), workerOptions.getObjectEncoder());

        Persistence persistence = new PersistenceImpl(dataObjectsRW, searchAttributeRW, stateLocals);
        CommandRequest commandRequest = state.getWorkflowState().start(
                context,
                input,
                persistence,
                communication);

        commandRequest.getCommands().forEach(cmd -> {
            if (cmd instanceof InterStateChannelCommand) {
                final String name = ((InterStateChannelCommand) cmd).getChannelName();
                if (communication.getToPublishInterStateChannels().containsKey(name)) {
                    throw new WorkflowDefinitionException("it's not allowed to publish and wait for the same interstate channel - " + name);
                }
            }
        });

        final WorkflowStateStartResponse response = new WorkflowStateStartResponse()
                .commandRequest(CommandRequestMapper.toGenerated(commandRequest));

        if (dataObjectsRW.getToReturnToServer().size() > 0) {
            response.upsertDataObjects(dataObjectsRW.getToReturnToServer());
        }
        if (stateLocals.getUpsertStateLocalAttributes().size() > 0) {
            response.upsertStateLocals(stateLocals.getUpsertStateLocalAttributes());
        }
        if (stateLocals.getRecordEvents().size() > 0) {
            response.recordEvents(stateLocals.getRecordEvents());
        }
        final List<SearchAttribute> upsertSAs = createUpsertSearchAttributes(
                searchAttributeRW.getUpsertToServerInt64AttributeMap(),
                searchAttributeRW.getUpsertToServerKeywordAttributeMap());
        if (upsertSAs.size() > 0) {
            response.upsertSearchAttributes(upsertSAs);
        }
        final List<InterStateChannelPublishing> interStateChannelPublishing = toInterStateChannelPublishing(communication.getToPublishInterStateChannels());
        if (interStateChannelPublishing.size() > 0) {
            response.publishToInterStateChannel(interStateChannelPublishing);
        }
        return response;
    }

    public WorkflowStateDecideResponse handleWorkflowStateDecide(final WorkflowStateDecideRequest req) {
        StateDef state = registry.getWorkflowState(req.getWorkflowType(), req.getWorkflowStateId());
        final Object input;
        final EncodedObject stateInput = req.getStateInput();
        input = workerOptions.getObjectEncoder().decode(stateInput, state.getWorkflowState().getInputType());
        final DataObjectsRWImpl dataObjectsRW =
                createQueryAttributesRW(req.getWorkflowType(), req.getDataObjects());

        final Context context = ImmutableContext.builder()
                .workflowId(req.getContext().getWorkflowId())
                .workflowRunId(req.getContext().getWorkflowRunId())
                .workflowStartTimestampSeconds(req.getContext().getWorkflowStartedTimestamp())
                .stateExecutionId(req.getContext().getStateExecutionId())
                .build();
        final StateLocalsImpl stateLocals = new StateLocalsImpl(toMap(req.getStateLocals()), workerOptions.getObjectEncoder());
        final SearchAttributeRWImpl searchAttributeRW = new SearchAttributeRWImpl(
                registry.getSearchAttributeKeyToTypeMap(req.getWorkflowType()), req.getSearchAttributes());
        final CommunicationImpl communication = new CommunicationImpl(
                registry.getInterStateChannelNameToTypeMap(req.getWorkflowType()), workerOptions.getObjectEncoder());

        Persistence persistence = new PersistenceImpl(dataObjectsRW, searchAttributeRW, stateLocals);

        StateDecision stateDecision = state.getWorkflowState().decide(
                context,
                input,
                CommandResultsMapper.fromGenerated(
                        req.getCommandResults(),
                        registry.getSignalChannelNameToSignalTypeMap(req.getWorkflowType()),
                        registry.getInterStateChannelNameToTypeMap(req.getWorkflowType()),
                        workerOptions.getObjectEncoder()),
                persistence,
                communication);

        final WorkflowStateDecideResponse response = new WorkflowStateDecideResponse()
                .stateDecision(StateDecisionMapper.toGenerated(stateDecision, req.getWorkflowType(), registry, workerOptions.getObjectEncoder()));

        if (dataObjectsRW.getToReturnToServer().size() > 0) {
            response.upsertDataObjects(dataObjectsRW.getToReturnToServer());
        }
        if (stateLocals.getUpsertStateLocalAttributes().size() > 0) {
            response.upsertStateLocals(stateLocals.getUpsertStateLocalAttributes());
        }
        if (stateLocals.getRecordEvents().size() > 0) {
            response.recordEvents(stateLocals.getRecordEvents());
        }
        final List<SearchAttribute> upsertSAs = createUpsertSearchAttributes(
                searchAttributeRW.getUpsertToServerInt64AttributeMap(),
                searchAttributeRW.getUpsertToServerKeywordAttributeMap());
        if (upsertSAs.size() > 0) {
            response.upsertSearchAttributes(upsertSAs);
        }
        final List<InterStateChannelPublishing> interStateChannelPublishing = toInterStateChannelPublishing(communication.getToPublishInterStateChannels());
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

    private DataObjectsRWImpl createQueryAttributesRW(String workflowType, List<KeyValue> keyValues) {
        final Map<String, EncodedObject> map = toMap(keyValues);
        return new DataObjectsRWImpl(registry.getQueryAttributeKeyToTypeMap(workflowType), map, workerOptions.getObjectEncoder());
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
            final Map<String, Long> upsertToServerInt64AttributeMap,
            final Map<String, String> upsertToServerKeywordAttributeMap) {
        List<SearchAttribute> sas = new ArrayList<>();
        upsertToServerKeywordAttributeMap.forEach((key, sa) -> {
            final SearchAttribute attr = new SearchAttribute()
                    .key(key)
                    .stringValue(sa)
                    .valueType(SearchAttribute.ValueTypeEnum.KEYWORD);
            sas.add(attr);
        });
        upsertToServerInt64AttributeMap.forEach((key, sa) -> {
            final SearchAttribute attr = new SearchAttribute()
                    .key(key)
                    .integerValue(sa)
                    .valueType(SearchAttribute.ValueTypeEnum.INT);
            sas.add(attr);
        });
        return sas;
    }
}
