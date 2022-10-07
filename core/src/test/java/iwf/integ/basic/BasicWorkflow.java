package iwf.integ.basic;

import iwf.core.StateDef;
import iwf.core.Workflow;

import java.util.Arrays;
import java.util.List;

public class BasicWorkflow implements Workflow {
    public List<StateDef> getStates() {
        return Arrays.asList(
                StateDef.startingState(new BasicWorkflowS1()),
                StateDef.normalState(new BasicWorkflowS2())
        );
    }
}
