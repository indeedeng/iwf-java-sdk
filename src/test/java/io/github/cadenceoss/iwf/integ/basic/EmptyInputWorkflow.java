package io.github.cadenceoss.iwf.integ.basic;

import io.github.cadenceoss.iwf.core.StateDef;
import io.github.cadenceoss.iwf.core.Workflow;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

@Component
public class EmptyInputWorkflow implements Workflow {

    public static final String CUSTOM_WF_TYPE = "test-customized-wf-type";

    @Override
    public List<StateDef> getStates() {
        return Arrays.asList(
                StateDef.startingState(new EmptyInputWorkflowState1()),
                StateDef.nonStartingState(new EmptyInputWorkflowState2())
        );
    }

    @Override
    public String getWorkflowType() {
        return CUSTOM_WF_TYPE;
    }
}
