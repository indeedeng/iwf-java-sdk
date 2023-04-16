package io.iworkflow.integ.basic;

import io.iworkflow.core.DEObject;
import io.iworkflow.core.StateDef;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

@Component
public class EmptyInputWorkflow implements DEObject {

    public static final String CUSTOM_WF_TYPE = "test-customized-wf-type";

    @Override
    public List<StateDef> getWorkflowStates() {
        return Arrays.asList(
                StateDef.startingState(new EmptyInputWorkflowState1()),
                StateDef.nonStartingState(new EmptyInputWorkflowState2())
        );
    }

    @Override
    public String getObjectType() {
        return CUSTOM_WF_TYPE;
    }
}
