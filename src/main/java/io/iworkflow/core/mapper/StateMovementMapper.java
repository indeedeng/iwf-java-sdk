package io.iworkflow.core.mapper;

import io.iworkflow.core.ObjectEncoder;
import io.iworkflow.core.Registry;
import io.iworkflow.core.StateDef;
import io.iworkflow.gen.models.StateMovement;
import io.iworkflow.gen.models.WorkflowStateOptions;

public class StateMovementMapper {

    public static StateMovement toGenerated(io.iworkflow.core.StateMovement stateMovement, final String workflowType, final Registry registry, final ObjectEncoder objectEncoder) {
        final Object input = stateMovement.getNextStateInput().orElse(null);
        final StateMovement movement = new StateMovement()
                .stateId(stateMovement.getStateId())
                .nextStateInput(objectEncoder.encode(input));
        if (!stateMovement.getStateId().startsWith("_SYS_")) {
            final StateDef stateDef = registry.getWorkflowState(workflowType, stateMovement.getStateId());
            final WorkflowStateOptions options = stateDef.getWorkflowState().getStateOptions();
            if (options != null) {
                movement.nextStateOptions(options);
            }
        }
        return movement;
    }
}
