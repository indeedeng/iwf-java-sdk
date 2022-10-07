package iwf.core.mapper;

import iwf.core.JacksonJsonObjectEncoder;
import iwf.core.ObjectEncoder;
import iwf.gen.models.EncodedObject;
import iwf.gen.models.StateMovement;

public class StateMovementMapper {

    private static final ObjectEncoder objectEncoder = new JacksonJsonObjectEncoder();

    public static StateMovement toGenerated(iwf.core.StateMovement stateMovement) {
        final Object input = stateMovement.getNextStateInput().orElse(null);
        final String data = objectEncoder.toData(input);
        return new StateMovement()
                .stateId(stateMovement.getStateId())
                .nextStateInput(
                        new EncodedObject()
                                .data(data)
                                .encoding(objectEncoder.getEncodingType())
                );
    }
}
