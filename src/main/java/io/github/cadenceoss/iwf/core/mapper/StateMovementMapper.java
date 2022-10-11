package io.github.cadenceoss.iwf.core.mapper;

import io.github.cadenceoss.iwf.core.JacksonJsonObjectEncoder;
import io.github.cadenceoss.iwf.core.ObjectEncoder;
import io.github.cadenceoss.iwf.gen.models.StateMovement;

public class StateMovementMapper {

    private static final ObjectEncoder objectEncoder = new JacksonJsonObjectEncoder();

    public static StateMovement toGenerated(io.github.cadenceoss.iwf.core.StateMovement stateMovement) {
        final Object input = stateMovement.getNextStateInput().orElse(null);
        return new StateMovement()
                .stateId(stateMovement.getStateId())
                .nextStateInput(objectEncoder.encode(input));
    }
}
