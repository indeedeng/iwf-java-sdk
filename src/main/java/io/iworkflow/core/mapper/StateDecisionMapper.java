package io.iworkflow.core.mapper;

import io.iworkflow.core.InternalConditionalClose;
import io.iworkflow.core.ObjectEncoder;
import io.iworkflow.core.Registry;
import io.iworkflow.gen.models.EncodedObject;
import io.iworkflow.gen.models.StateDecision;
import io.iworkflow.gen.models.WorkflowConditionalClose;

import java.util.stream.Collectors;

public class StateDecisionMapper {
    public static StateDecision toGenerated(io.iworkflow.core.StateDecision stateDecision, final String workflowType, final Registry registry, final ObjectEncoder objectEncoder) {
        if (stateDecision.getNextStates() == null && !stateDecision.getWorkflowConditionalClose().isPresent()) {
            return null;
        }

        StateDecision decision = new StateDecision();

        if (stateDecision.getNextStates() != null) {
            decision.nextStates(
                    stateDecision.getNextStates()
                            .stream()
                            .map(movement -> StateMovementMapper.toGenerated(movement, workflowType, registry, objectEncoder))
                            .collect(Collectors.toList())
            );
        }

        if (!stateDecision.getWorkflowConditionalClose().isPresent()) {
            return decision;
        }

        InternalConditionalClose conditionalClose = stateDecision.getWorkflowConditionalClose().get();
        EncodedObject closeInput = objectEncoder.encode(conditionalClose.getCloseInput());
        decision.conditionalClose(
                new WorkflowConditionalClose()
                        .conditionalCloseType(conditionalClose.getWorkflowConditionalCloseType())
                        .closeInput(closeInput)
                        .channelName(conditionalClose.getChannelName())
        );
        return decision;
    }
}
