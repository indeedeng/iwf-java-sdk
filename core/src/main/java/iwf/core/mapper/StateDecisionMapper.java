package iwf.core.mapper;

import iwf.gen.models.StateDecision;

import java.util.stream.Collectors;

public class StateDecisionMapper {
    public static StateDecision toGenerated(iwf.core.StateDecision stateDecision) {
        if (!stateDecision.getNextStates().isPresent()) {
            return null;
        }
        return new StateDecision().nextStates(
                stateDecision.getNextStates()
                        .get().stream()
                        .map(StateMovementMapper::toGenerated)
                        .collect(Collectors.toList()));
    }
}
