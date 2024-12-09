package io.iworkflow.integ.basic;

import io.iworkflow.core.ObjectWorkflow;
import io.iworkflow.core.StateDef;
import io.iworkflow.gen.models.RetryPolicy;
import io.iworkflow.gen.models.WorkflowStateOptions;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

@Component
public class MixOfWithWaitUntilAndSkipWaitUntilWorkflow implements ObjectWorkflow {

    public static WorkflowStateOptions SHARED_STATE_OPTIONS = new WorkflowStateOptions()
                .executeApiRetryPolicy(
                        new RetryPolicy().maximumAttempts(3)
                );

    @Override
    public List<StateDef> getWorkflowStates() {
        return Arrays.asList(
                StateDef.startingState(new SkipWaitUntilState2()),
                StateDef.nonStartingState(new RegularState2())
        );
    }
}
