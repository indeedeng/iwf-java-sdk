package iwf.core;

import iwf.core.command.CommandRequest;
import iwf.core.mapper.CommandRequestMapper;
import iwf.core.mapper.StateDecisionMapper;
import iwf.gen.models.EncodedObject;
import iwf.gen.models.WorkflowStateDecideRequest;
import iwf.gen.models.WorkflowStateDecideResponse;
import iwf.gen.models.WorkflowStateStartRequest;
import iwf.gen.models.WorkflowStateStartResponse;

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
        try {
            input = objectEncoder.fromData(stateInput.getData(), state.getWorkflowState().getInputType(), state.getWorkflowState().getInputType());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        CommandRequest commandRequest = state.getWorkflowState().start(null, input, null, null, null);
        return new WorkflowStateStartResponse().commandRequest(CommandRequestMapper.toGenerated(commandRequest));
    }

    public WorkflowStateDecideResponse handleWorkflowStateDecide(final WorkflowStateDecideRequest req) {
        StateDef state = registry.getWorkflowState(req.getWorkflowType(), req.getWorkflowStateId());
        final Object input;
        final EncodedObject stateInput = req.getStateInput();
        try {
            input = objectEncoder.fromData(stateInput.getData(), state.getWorkflowState().getInputType(), state.getWorkflowState().getInputType());
        } catch (ObjectEncoderException e) {
            throw new RuntimeException(e);
        }
        StateDecision stateDecision = state.getWorkflowState().decide(null, input, null, null, null, null);
        return new WorkflowStateDecideResponse().stateDecision(StateDecisionMapper.toGenerated(stateDecision));
    }
}
