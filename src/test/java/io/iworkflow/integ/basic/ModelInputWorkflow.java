package io.iworkflow.integ.basic;

import io.iworkflow.core.StateDef;
import io.iworkflow.core.Workflow;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

@Component
public class ModelInputWorkflow implements Workflow {
    @Override
    public List<StateDef> getStates() {
        return Arrays.asList(
                StateDef.startingState(new ModelInputWorkflowState1())
        );
    }
}
