package iwf.core.mapper;

import iwf.gen.models.StateMovement;

public class StateMovementMapper {
    public static StateMovement toGenerated(iwf.core.StateMovement stateMovement) {
        return new StateMovement().stateId(stateMovement.getStateId());
    }
}
