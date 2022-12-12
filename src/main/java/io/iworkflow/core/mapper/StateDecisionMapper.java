package io.iworkflow.core.mapper;

import io.iworkflow.core.ObjectEncoder;
import io.iworkflow.gen.models.StateDecision;

import java.util.stream.Collectors;

public class StateDecisionMapper {
    public static StateDecision toGenerated(io.iworkflow.core.StateDecision stateDecision, final ObjectEncoder objectEncoder) {
        if (stateDecision.getNextStates() == null) {
            return null;
        }
        return new StateDecision()
                .nextStates(stateDecision.getNextStates()
                        .stream()
                        .map(e -> {
                            return StateMovementMapper.toGenerated(e, objectEncoder);
                        })
                        .collect(Collectors.toList()));
    }
}
