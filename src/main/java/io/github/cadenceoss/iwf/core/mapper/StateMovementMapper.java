package io.github.cadenceoss.iwf.core.mapper;

import io.github.cadenceoss.iwf.core.ObjectEncoder;
import io.github.cadenceoss.iwf.gen.models.StateMovement;

public class StateMovementMapper {
    
    public static StateMovement toGenerated(io.github.cadenceoss.iwf.core.StateMovement stateMovement, final ObjectEncoder objectEncoder) {
        final Object input = stateMovement.getNextStateInput().orElse(null);
        return new StateMovement()
                .stateId(stateMovement.getStateId())
                .nextStateInput(objectEncoder.encode(input));
    }
}
