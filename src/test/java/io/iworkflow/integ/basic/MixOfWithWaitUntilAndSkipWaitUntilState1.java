package io.iworkflow.integ.basic;

import io.iworkflow.core.Context;
import io.iworkflow.core.StateDecision;
import io.iworkflow.core.WorkflowState;
import io.iworkflow.core.WorkflowStateOptions;
import io.iworkflow.core.command.CommandResults;
import io.iworkflow.core.communication.Communication;
import io.iworkflow.core.persistence.Persistence;

import static io.iworkflow.integ.basic.MixOfWithWaitUntilAndSkipWaitUntilWorkflow.SHARED_STATE_OPTIONS;

public class MixOfWithWaitUntilAndSkipWaitUntilState1 implements WorkflowState<Integer> {

    @Override
    public Class<Integer> getInputType() {
        return Integer.class;
    }

    @Override
    public StateDecision execute(final Context context, final Integer input, final CommandResults commandResults, Persistence persistence, final Communication communication) {
        final int output = input + 1;
        return StateDecision.singleNextState(MixOfWithWaitUntilAndSkipWaitUntilState2.class, output);
    }

    @Override
    public WorkflowStateOptions getStateOptions() {
        return SHARED_STATE_OPTIONS;
    }
}