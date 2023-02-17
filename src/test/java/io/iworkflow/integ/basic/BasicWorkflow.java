package io.iworkflow.integ.basic;

import io.iworkflow.core.StateDef;
import io.iworkflow.core.Workflow;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

@Component
public class BasicWorkflow implements Workflow {

    @Override
    public List<StateDef> getStates() {
        return Arrays.asList(
                StateDef.startingState(new BasicWorkflowState1()),
                StateDef.nonStartingState(new BasicWorkflowState2()),
                StateDef.nonStartingState(new BasicWorkflowState3())
        );
    }
}
