package io.iworkflow.integ.basic;

import io.iworkflow.core.Context;
import io.iworkflow.core.StateDecision;
import io.iworkflow.core.WorkflowState;
import io.iworkflow.core.command.CommandRequest;
import io.iworkflow.core.command.CommandResults;
import io.iworkflow.core.command.TimerCommand;
import io.iworkflow.core.communication.Communication;
import io.iworkflow.core.persistence.Persistence;
import io.iworkflow.gen.models.PersistenceLoadingPolicy;
import io.iworkflow.gen.models.PersistenceLoadingType;
import io.iworkflow.gen.models.RetryPolicy;
import io.iworkflow.gen.models.TimerStatus;
import io.iworkflow.gen.models.WorkflowStateOptions;
import io.iworkflow.integ.stateoptions.StateOptionsWorkflow;

import java.time.Duration;
import java.util.Collections;

import static io.iworkflow.integ.basic.MixOfWithWaitUntilAndSkipWaitUntilWorkflow.SHARED_STATE_OPTIONS;

public class RegularState2 implements WorkflowState<Integer> {

    @Override
    public Class<Integer> getInputType() {
        return Integer.class;
    }

    @Override
    public CommandRequest waitUntil(final Context context, final Integer input, Persistence persistence, final Communication communication) {
        return CommandRequest.forAllCommandCompleted(TimerCommand.createByDuration(Duration.ofSeconds(1)));
    }

    @Override
    public StateDecision execute(final Context context, final Integer input, final CommandResults commandResults, Persistence persistence, final Communication communication) {
        final int output = input + 1;
        final TimerStatus status = commandResults.getAllTimerCommandResults().get(0).getTimerStatus();
        System.out.println(status);
        return StateDecision.gracefulCompleteWorkflow(output);
    }

    @Override
    public WorkflowStateOptions getStateOptions() {
        return SHARED_STATE_OPTIONS;
    }
}