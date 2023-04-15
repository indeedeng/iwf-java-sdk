package io.iworkflow.integ.timer;

import io.iworkflow.core.DEObject;
import io.iworkflow.core.StateDef;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

@Component
public class BasicTimerWorkflow implements DEObject {
    @Override
    public List<StateDef> getWorkflowStates() {
        return Arrays.asList(
                StateDef.startingState(new BasicTimerWorkflowState1())
        );
    }
}
