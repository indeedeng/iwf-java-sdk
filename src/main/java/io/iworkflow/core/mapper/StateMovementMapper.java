package io.iworkflow.core.mapper;

import io.iworkflow.core.ObjectEncoder;
import io.iworkflow.gen.models.StateMovement;

public class StateMovementMapper {
    
    public static StateMovement toGenerated(io.iworkflow.core.StateMovement stateMovement, final ObjectEncoder objectEncoder) {
        final Object input = stateMovement.getNextStateInput().orElse(null);
        return new StateMovement()
                .stateId(stateMovement.getStateId())
                .nextStateInput(objectEncoder.encode(input));
    }
}
