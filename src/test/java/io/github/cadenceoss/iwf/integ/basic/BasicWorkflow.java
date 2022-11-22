package io.github.cadenceoss.iwf.integ.basic;

import io.github.cadenceoss.iwf.core.StateDef;
import io.github.cadenceoss.iwf.core.Workflow;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

@Component
public class BasicWorkflow implements Workflow {

    @Override
    public List<StateDef> getStates() {
        return Arrays.asList(
                StateDef.startingState(new BasicWorkflowState1()),
                StateDef.nonStartingState(new BasicWorkflowState2())
        );
    }
}
