package io.github.cadenceoss.iwf.core;

import io.github.cadenceoss.iwf.core.attributes.QueryAttributesRW;
import io.github.cadenceoss.iwf.core.attributes.QueryAttributesRWImpl;
import io.github.cadenceoss.iwf.core.mapper.CommandRequestMapper;
import io.github.cadenceoss.iwf.core.mapper.CommandResultsMapper;
import io.github.cadenceoss.iwf.gen.models.*;
import io.github.cadenceoss.iwf.core.command.CommandRequest;
import io.github.cadenceoss.iwf.core.mapper.StateDecisionMapper;

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
                queryAttributesRW);
        
        return new WorkflowStateStartResponse()
                .commandRequest(CommandRequestMapper.toGenerated(commandRequest))
                .upsertQueryAttributes(queryAttributesRW != null ? queryAttributesRW.getUpsertQueryAttributes() : null);
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
                queryAttributesRW);
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

        return new QueryAttributesRWImpl(registry.getQueryAttributeNameToTypeMap(workflowType), map, objectEncoder);
    }
}
