package io.github.cadenceoss.iwf.integ.basic;

import io.github.cadenceoss.iwf.core.StateDef;
import io.github.cadenceoss.iwf.core.Workflow;

import java.util.List;

public class BasicSignalWorkflow implements Workflow {
    @Override
    public List<StateDef> getStates() {
        return List.of(
                StateDef.startingState(new BasicSignalWorkflowState1())
        );
    }
}
