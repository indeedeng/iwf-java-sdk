package io.iworkflow.integ.statedecision;

import io.iworkflow.core.Context;
import io.iworkflow.core.ObjectWorkflow;
import io.iworkflow.core.StateDecision;
import io.iworkflow.core.StateDef;
import io.iworkflow.core.StateMovement;
import io.iworkflow.core.WorkflowState;
import io.iworkflow.core.command.CommandResults;
import io.iworkflow.core.communication.Communication;
import io.iworkflow.core.persistence.Persistence;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@Component
public class EmptyStateDecisionWorkflow implements ObjectWorkflow {
    @Override
    public List<StateDef> getWorkflowStates() {
        return Arrays.asList(
                StateDef.startingState(new EmptyStateDecisionState1())
        );
    }
}

class EmptyStateDecisionState1 implements WorkflowState<Integer> {
    @Override
    public Class<Integer> getInputType() {
        return Integer.class;
    }

    @Override
    public StateDecision execute(
            Context context,
            Integer useSignal,
            CommandResults commandResults,
            Persistence persistence,
            final Communication communication
    ) {
        List<StateMovement> emptyList = Collections.emptyList();
        return StateDecision.multiNextStates(emptyList);
    }
}