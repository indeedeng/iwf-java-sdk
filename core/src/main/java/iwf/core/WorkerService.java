package iwf.core;

import iwf.core.command.CommandRequest;
import iwf.core.mapper.CommandRequestMapper;
import iwf.core.mapper.StateDecisionMapper;
import iwf.gen.models.WorkflowStateDecideRequest;
import iwf.gen.models.WorkflowStateDecideResponse;
import iwf.gen.models.WorkflowStateStartRequest;
import iwf.gen.models.WorkflowStateStartResponse;

public class WorkerService {
    private final Registry registry;

    public WorkerService(Registry registry) {
        this.registry = registry;
    }

    public WorkflowStateStartResponse handleWorkflowStateStart(final WorkflowStateStartRequest req) {
        StateDef state = registry.getWorkflowState(req.getWorkflowType(), req.getWorkflowStateId());
        CommandRequest commandRequest = state.getWorkflowState().start(null, null, null, null, null);
        return new WorkflowStateStartResponse().commandRequest(CommandRequestMapper.toGenerated(commandRequest));
    }

    public WorkflowStateDecideResponse handleWorkflowStateDecide(final WorkflowStateDecideRequest req) {
        StateDef state = registry.getWorkflowState(req.getWorkflowType(), req.getWorkflowStateId());
        StateDecision stateDecision = state.getWorkflowState().decide(null, null, null, null, null, null);
        return new WorkflowStateDecideResponse().stateDecision(StateDecisionMapper.toGenerated(stateDecision));
    }
}
