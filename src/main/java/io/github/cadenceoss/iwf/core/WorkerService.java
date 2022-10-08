package io.github.cadenceoss.iwf.core;

import io.github.cadenceoss.iwf.core.mapper.CommandRequestMapper;
import io.github.cadenceoss.iwf.gen.models.WorkflowStateDecideResponse;
import io.github.cadenceoss.iwf.core.command.CommandRequest;
import io.github.cadenceoss.iwf.core.mapper.StateDecisionMapper;
import io.github.cadenceoss.iwf.gen.models.EncodedObject;
import io.github.cadenceoss.iwf.gen.models.WorkflowStateDecideRequest;
import io.github.cadenceoss.iwf.gen.models.WorkflowStateStartRequest;
import io.github.cadenceoss.iwf.gen.models.WorkflowStateStartResponse;

public class WorkerService {
    private final Registry registry;

    private final ObjectEncoder objectEncoder = new JacksonJsonObjectEncoder();

    public WorkerService(Registry registry) {
        this.registry = registry;
    }

    public WorkflowStateStartResponse handleWorkflowStateStart(final WorkflowStateStartRequest req) {
        StateDef state = registry.getWorkflowState(req.getWorkflowType(), req.getWorkflowStateId());
        final EncodedObject stateInput = req.getStateInput();
        final Object input;
            input = objectEncoder.fromData(stateInput.getData(), state.getWorkflowState().getInputType());
        CommandRequest commandRequest = state.getWorkflowState().start(null, input, null, null, null);
        
        return new WorkflowStateStartResponse().commandRequest(CommandRequestMapper.toGenerated(commandRequest));
    }

    public WorkflowStateDecideResponse handleWorkflowStateDecide(final WorkflowStateDecideRequest req) {
        StateDef state = registry.getWorkflowState(req.getWorkflowType(), req.getWorkflowStateId());
        final Object input;
        final EncodedObject stateInput = req.getStateInput();
        input = objectEncoder.fromData(stateInput.getData(), state.getWorkflowState().getInputType());

        StateDecision stateDecision = state.getWorkflowState().decide(null, input, null, null, null, null);
        return new WorkflowStateDecideResponse().stateDecision(StateDecisionMapper.toGenerated(stateDecision));
    }
}
