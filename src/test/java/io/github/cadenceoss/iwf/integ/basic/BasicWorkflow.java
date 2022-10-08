package io.github.cadenceoss.iwf.integ.basic;

import io.github.cadenceoss.iwf.core.StateDef;
import io.github.cadenceoss.iwf.core.Workflow;

import java.util.Arrays;
import java.util.List;

public class BasicWorkflow implements Workflow {
    @Override
    public List<StateDef> getStates() {
        return Arrays.asList(
                StateDef.startingState(new BasicWorkflowS1()),
                StateDef.nonStartingState(new BasicWorkflowS2())
        );
    }
}
