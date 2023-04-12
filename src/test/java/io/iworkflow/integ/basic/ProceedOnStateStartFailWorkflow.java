package io.iworkflow.integ.basic;

import io.iworkflow.core.ObjectWorkflow;
import io.iworkflow.core.StateDef;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

@Component
public class ProceedOnStateStartFailWorkflow implements ObjectWorkflow {
    @Override
    public List<StateDef> getWorkflowStates() {
        return Arrays.asList(
                StateDef.startingState(new ProceedOnStateStartFailWorkflowState1()),
                StateDef.nonStartingState(new ProceedOnStateStartFailWorkflowState2())
        );
    }
}
