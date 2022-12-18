package io.iworkflow.core.mapper;

import io.iworkflow.core.ObjectEncoder;
import io.iworkflow.core.Registry;
import io.iworkflow.core.StateDef;
import io.iworkflow.gen.models.StateMovement;
import io.iworkflow.gen.models.WorkflowStateOptions;

import static io.iworkflow.core.StateMovement.RESERVED_STATE_ID_PREFIX;

public class StateMovementMapper {

    public static StateMovement toGenerated(io.iworkflow.core.StateMovement stateMovement, final String workflowType, final Registry registry, final ObjectEncoder objectEncoder) {
        final Object input = stateMovement.getStateInput().orElse(null);
        final StateMovement movement = new StateMovement()
                .stateId(stateMovement.getStateId())
                .stateInput(objectEncoder.encode(input));
        if (!stateMovement.getStateId().startsWith(RESERVED_STATE_ID_PREFIX)) {
            final StateDef stateDef = registry.getWorkflowState(workflowType, stateMovement.getStateId());
            final WorkflowStateOptions options = stateDef.getWorkflowState().getStateOptions();
            if (options != null) {
                movement.stateOptions(options);
            }
        }
        return movement;
    }
}
