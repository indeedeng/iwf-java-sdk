package io.iworkflow.integ.anycommandcombination;

import io.iworkflow.core.ObjectWorkflow;
import io.iworkflow.core.StateDef;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;

@Component
public class AnyCommandCombinationFailWorkflow implements ObjectWorkflow {
    @Override
    public List<StateDef> getWorkflowStates() {
        return Collections.singletonList(
                StateDef.startingState(new InvalidAnyCommandCombinationWorkflowState())
        );
    }
}
