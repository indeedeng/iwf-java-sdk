package io.iworkflow.integ.stateapitimeout;

import io.iworkflow.core.DEObject;
import io.iworkflow.core.StateDef;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

@Component
public class StateApiTimeoutFailWorkflow implements DEObject {
    @Override
    public List<StateDef> getWorkflowStates() {
        return Arrays.asList(
                StateDef.startingState(new StateApiTimeoutWorkflowState1())
        );
    }
}
