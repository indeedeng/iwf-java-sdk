package io.iworkflow.integ.basic;

import io.iworkflow.core.DEObject;
import io.iworkflow.core.StateDef;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

@Component
public class ModelInputWorkflow implements DEObject {
    @Override
    public List<StateDef> getWorkflowStates() {
        return Arrays.asList(
                StateDef.startingState(new ModelInputWorkflowState1())
        );
    }
}
