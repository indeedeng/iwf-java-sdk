package iwf.integ.basic;

import iwf.core.ImmutableStateDef;
import iwf.core.StateDef;
import iwf.core.Workflow;

import java.util.Arrays;
import java.util.List;

public class BasicWorkflow implements Workflow {
    @Override
    public List<StateDef> getStates() {
        return Arrays.asList(
                ImmutableStateDef.builder()
                        .canStartWorkflow(true)
                        .workflowState(
                                new BasicWorkflowS1()
                        )
                        .build()
        );
    }
}
