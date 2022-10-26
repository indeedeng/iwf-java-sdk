package io.github.cadenceoss.iwf.integ.timer;

import io.github.cadenceoss.iwf.core.StateDef;
import io.github.cadenceoss.iwf.core.Workflow;

import java.util.Arrays;
import java.util.List;

public class BasicTimerWorkflow implements Workflow {
    @Override
    public List<StateDef> getStates() {
        return Arrays.asList(
                StateDef.startingState(new BasicTimerWorkflowState1())
        );
    }
}
