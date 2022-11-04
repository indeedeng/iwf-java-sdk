package io.github.cadenceoss.iwf.core;

import io.github.cadenceoss.iwf.core.attributes.QueryAttributesRW;
import io.github.cadenceoss.iwf.core.command.CommandRequest;
import io.github.cadenceoss.iwf.core.mapper.CommandRequestMapper;
import io.github.cadenceoss.iwf.core.mapper.CommandResultsMapper;
import io.github.cadenceoss.iwf.core.mapper.StateDecisionMapper;
import io.github.cadenceoss.iwf.gen.models.EncodedObject;
import io.github.cadenceoss.iwf.gen.models.KeyValue;
import io.github.cadenceoss.iwf.gen.models.WorkflowStateDecideRequest;
import io.github.cadenceoss.iwf.gen.models.WorkflowStateDecideResponse;
import io.github.cadenceoss.iwf.gen.models.WorkflowStateStartRequest;
import io.github.cadenceoss.iwf.gen.models.WorkflowStateStartResponse;

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
        final QueryAttributesRW queryAttributesRW =
                createQueryAttributesRW(req.getWorkflowType(), req.getQueryAttributes());

        CommandRequest commandRequest = state.getWorkflowState().start(
                null,
                input,
                null,
                null,
                queryAttributesRW,
                null);
        
        return new WorkflowStateStartResponse()
                .commandRequest(CommandRequestMapper.toGenerated(commandRequest))
                .upsertQueryAttributes(queryAttributesRW.getUpsertQueryAttributes());
    }

    public WorkflowStateDecideResponse handleWorkflowStateDecide(final WorkflowStateDecideRequest req) {
        StateDef state = registry.getWorkflowState(req.getWorkflowType(), req.getWorkflowStateId());
        final Object input;
        final EncodedObject stateInput = req.getStateInput();
        input = objectEncoder.decode(stateInput, state.getWorkflowState().getInputType());
        final QueryAttributesRW queryAttributesRW =
                createQueryAttributesRW(req.getWorkflowType(), req.getQueryAttributes());

        StateDecision stateDecision = state.getWorkflowState().decide(
                null,
                input,
                CommandResultsMapper.fromGenerated(
                        req.getCommandResults(),
                        registry.getSignalChannelNameToSignalTypeMap(req.getWorkflowType()),
                        objectEncoder),
                null,
                null,
                queryAttributesRW,
                null);
        List<KeyValue> queryAttributesToUpsert = queryAttributesRW.getUpsertQueryAttributes();
        stateDecision = ImmutableStateDecision.copyOf(stateDecision).withUpsertQueryAttributes(queryAttributesToUpsert);
        return new WorkflowStateDecideResponse()
                .stateDecision(StateDecisionMapper.toGenerated(stateDecision));
    }

    private QueryAttributesRW createQueryAttributesRW(String workflowType, List<KeyValue> keyValues) {
        Map<String, EncodedObject> map;
        if (keyValues == null || keyValues.isEmpty()) {
            map = new HashMap<>();
        } else {
            map = keyValues.stream()
                    .filter(keyValue -> keyValue.getValue() != null)
                    .collect(Collectors.toMap(KeyValue::getKey, KeyValue::getValue));
        }

        return new QueryAttributesRW(registry.getQueryAttributeKeyToTypeMap(workflowType), map, objectEncoder);
    }
}
