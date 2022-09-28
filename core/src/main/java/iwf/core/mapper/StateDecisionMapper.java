package iwf.core.mapper;

import iwf.gen.models.StateDecision;

import java.util.stream.Collectors;

public class StateDecisionMapper {
    public static StateDecision toGenerated(iwf.core.StateDecision stateDecision) {
        return new StateDecision().nextStates(
                stateDecision.getNextStates()
                        .stream()
                        .map(StateMovementMapper::toGenerated)
                        .collect(Collectors.toList()));
    }
}
