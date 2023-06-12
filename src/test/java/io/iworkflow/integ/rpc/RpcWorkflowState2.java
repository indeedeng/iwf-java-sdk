package io.iworkflow.integ.rpc;

import io.iworkflow.core.Context;
import io.iworkflow.core.StateDecision;
import io.iworkflow.core.WorkflowState;
import io.iworkflow.core.command.CommandRequest;
import io.iworkflow.core.command.CommandResults;
import io.iworkflow.core.communication.Communication;
import io.iworkflow.core.persistence.Persistence;

import java.util.Optional;

import static io.iworkflow.integ.rpc.Keys.*;

public class RpcWorkflowState2 implements WorkflowState<Integer> {




    @Override
    public Class<Integer> getInputType() {
        return Integer.class;
    }

    @Override
    public CommandRequest waitUntil(
            Context context,
            Integer input,
            Persistence persistence,
            final Communication communication) {
        return CommandRequest.empty;
    }

    @Override
    public synchronized StateDecision execute(
            Context context,
            Integer input,
            CommandResults commandResults,
            Persistence persistence,
            final Communication communication) {
        Integer counter = Optional.ofNullable(
                persistence.getDataAttribute(COUNTER_KEY, Integer.class)
        ).orElse(0);
        Integer maxDecisionCount = Optional.ofNullable(
                persistence.getDataAttribute(MAX_COUNTER, Integer.class)
        ).orElse(Integer.MAX_VALUE);
        counter ++;
        persistence.setDataAttribute(COUNTER_KEY, counter);
        counter++;
        if (counter == maxDecisionCount) {
            return StateDecision.gracefulCompleteWorkflow(counter);
        } else {
            return StateDecision.gracefulCompleteWorkflow();
        }
    }

}
