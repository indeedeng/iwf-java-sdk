package io.github.cadenceoss.iwf.core;

import io.github.cadenceoss.iwf.core.attributes.QueryAttributesRWImpl;
import io.github.cadenceoss.iwf.core.attributes.SearchAttributeRWImpl;
import io.github.cadenceoss.iwf.core.attributes.StateLocalImpl;
import io.github.cadenceoss.iwf.core.command.CommandRequest;
import io.github.cadenceoss.iwf.core.command.InterStateChannelCommand;
import io.github.cadenceoss.iwf.core.command.InterStateChannelImpl;
import io.github.cadenceoss.iwf.core.mapper.CommandRequestMapper;
import io.github.cadenceoss.iwf.core.mapper.CommandResultsMapper;
import io.github.cadenceoss.iwf.core.mapper.StateDecisionMapper;
import io.github.cadenceoss.iwf.gen.models.EncodedObject;
import io.github.cadenceoss.iwf.gen.models.InterStateChannelPublishing;
import io.github.cadenceoss.iwf.gen.models.KeyValue;
import io.github.cadenceoss.iwf.gen.models.SearchAttribute;
import io.github.cadenceoss.iwf.gen.models.WorkflowStateDecideRequest;
import io.github.cadenceoss.iwf.gen.models.WorkflowStateDecideResponse;
import io.github.cadenceoss.iwf.gen.models.WorkflowStateStartRequest;
import io.github.cadenceoss.iwf.gen.models.WorkflowStateStartResponse;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class WorkerService {
    private final Registry registry;

    private final ObjectEncoder objectEncoder = new JacksonJsonObjectEncoder();

    public WorkerService(Registry registry) {
        this.registry = registry;
    }

    public WorkflowStateStartResponse handleWorkflowStateStart(final WorkflowStateStartRequest req) {
        StateDef state = registry.getWorkflowState(req.getWorkflowType(), req.getWorkflowStateId());
        final EncodedObject stateInput = req.getStateInput();
        final Object input = objectEncoder.decode(stateInput, state.getWorkflowState().getInputType());
        final QueryAttributesRWImpl queryAttributesRW =
                createQueryAttributesRW(req.getWorkflowType(), req.getQueryAttributes());
        final Context context = ImmutableContext.builder()
                .workflowId(req.getContext().getWorkflowId())
                .workflowRunId(req.getContext().getWorkflowRunId())
                .workflowStartTimestampSeconds(req.getContext().getWorkflowStartedTimestamp())
                .stateExecutionId(req.getContext().getStateExecutionId())
                .build();
        final StateLocalImpl stateLocals = new StateLocalImpl(toMap(null), objectEncoder);
        final SearchAttributeRWImpl searchAttributeRW = new SearchAttributeRWImpl(
                registry.getSearchAttributeKeyToTypeMap(req.getWorkflowType()), req.getSearchAttributes());
        final InterStateChannelImpl interStateChannel = new InterStateChannelImpl(
                registry.getInterStateChannelNameToTypeMap(req.getWorkflowType()), objectEncoder);

        CommandRequest commandRequest = state.getWorkflowState().start(
                context,
                input,
                stateLocals,
                searchAttributeRW,
                queryAttributesRW,
                interStateChannel);

        commandRequest.getCommands().forEach(cmd -> {
            if (cmd instanceof InterStateChannelCommand) {
                final String name = ((InterStateChannelCommand) cmd).getChannelName();
                if (interStateChannel.getToPublish().containsKey(name)) {
                    throw new WorkflowDefinitionException("it's not allowed to publish and wait for the same interstate channel - " + name);
                }
            }
        });

        return new WorkflowStateStartResponse()
                .commandRequest(CommandRequestMapper.toGenerated(commandRequest))
                .upsertQueryAttributes(queryAttributesRW.getUpsertQueryAttributes())
                .upsertStateLocalAttributes(stateLocals.getUpsertStateLocalAttributes())
                .recordEvents(stateLocals.getRecordEvents())
                .upsertSearchAttributes(createUpsertSearchAttributes(
                        searchAttributeRW.getUpsertToServerInt64AttributeMap(),
                        searchAttributeRW.getUpsertToServerKeywordAttributeMap()))
                .publishToInterStateChannel(toInterStateChannelPublishing(interStateChannel.getToPublish()));
    }

    public WorkflowStateDecideResponse handleWorkflowStateDecide(final WorkflowStateDecideRequest req) {
        StateDef state = registry.getWorkflowState(req.getWorkflowType(), req.getWorkflowStateId());
        final Object input;
        final EncodedObject stateInput = req.getStateInput();
        input = objectEncoder.decode(stateInput, state.getWorkflowState().getInputType());
        final QueryAttributesRWImpl queryAttributesRW =
                createQueryAttributesRW(req.getWorkflowType(), req.getQueryAttributes());

        final Context context = ImmutableContext.builder()
                .workflowId(req.getContext().getWorkflowId())
                .workflowRunId(req.getContext().getWorkflowRunId())
                .workflowStartTimestampSeconds(req.getContext().getWorkflowStartedTimestamp())
                .stateExecutionId(req.getContext().getStateExecutionId())
                .build();
        final StateLocalImpl stateLocals = new StateLocalImpl(toMap(req.getStateLocalAttributes()), objectEncoder);
        final SearchAttributeRWImpl searchAttributeRW = new SearchAttributeRWImpl(
                registry.getSearchAttributeKeyToTypeMap(req.getWorkflowType()), req.getSearchAttributes());
        final InterStateChannelImpl interStateChannel = new InterStateChannelImpl(
                registry.getInterStateChannelNameToTypeMap(req.getWorkflowType()), objectEncoder);

        StateDecision stateDecision = state.getWorkflowState().decide(
                context,
                input,
                CommandResultsMapper.fromGenerated(
                        req.getCommandResults(),
                        registry.getSignalChannelNameToSignalTypeMap(req.getWorkflowType()),
                        objectEncoder),
                stateLocals,
                searchAttributeRW,
                queryAttributesRW,
                interStateChannel);

        return new WorkflowStateDecideResponse()
                .stateDecision(StateDecisionMapper.toGenerated(stateDecision))
                .upsertQueryAttributes(queryAttributesRW.getUpsertQueryAttributes())
                .upsertStateLocalAttributes(stateLocals.getUpsertStateLocalAttributes())
                .recordEvents(stateLocals.getRecordEvents())
                .upsertSearchAttributes(createUpsertSearchAttributes(
                        searchAttributeRW.getUpsertToServerInt64AttributeMap(),
                        searchAttributeRW.getUpsertToServerKeywordAttributeMap()))
                .publishToInterStateChannel(toInterStateChannelPublishing(interStateChannel.getToPublish()));
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

    private QueryAttributesRWImpl createQueryAttributesRW(String workflowType, List<KeyValue> keyValues) {
        final Map<String, EncodedObject> map = toMap(keyValues);
        return new QueryAttributesRWImpl(registry.getQueryAttributeKeyToTypeMap(workflowType), map, objectEncoder);
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
