package io.iworkflow.integ.anycommandcombination;

import io.iworkflow.core.Context;
import io.iworkflow.core.StateDecision;
import io.iworkflow.core.command.CommandRequest;
import io.iworkflow.core.command.CommandResults;
import io.iworkflow.core.command.TimerCommand;
import io.iworkflow.core.communication.Communication;
import io.iworkflow.core.communication.SignalCommand;
import io.iworkflow.core.persistence.Persistence;
import io.iworkflow.gen.models.RetryPolicy;
import io.iworkflow.gen.models.WorkflowStateOptions;

import java.time.Duration;
import java.util.Arrays;
import java.util.Collections;

import static io.iworkflow.integ.signal.BasicSignalWorkflow.SIGNAL_CHANNEL_NAME_1;
import static io.iworkflow.integ.signal.BasicSignalWorkflow.SIGNAL_CHANNEL_NAME_2;
import static io.iworkflow.integ.signal.BasicSignalWorkflow.SIGNAL_CHANNEL_NAME_3;

public class InvalidAnyCommandCombinationWorkflowState implements io.iworkflow.core.WorkflowState<Integer> {
    public static final String SIGNAL_COMMAND_ID_1 = "test-signal-1";
    public static final String SIGNAL_COMMAND_ID_2 = "test-signal-2";
    public static final String SIGNAL_COMMAND_ID_3 = "test-signal-3";
    public static final String TIMER_COMMAND_ID = "test-timer-id";

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
        return CommandRequest.forAnyCommandCombinationCompleted(
                Collections.singletonList(
                        Arrays.asList(SIGNAL_COMMAND_ID_1, SIGNAL_COMMAND_ID_3, TIMER_COMMAND_ID)
                ),
                SignalCommand.create(SIGNAL_COMMAND_ID_1, SIGNAL_CHANNEL_NAME_1),
                SignalCommand.create(SIGNAL_COMMAND_ID_1, SIGNAL_CHANNEL_NAME_2),
                SignalCommand.create(SIGNAL_COMMAND_ID_2, SIGNAL_CHANNEL_NAME_3),
                TimerCommand.createByDuration(TIMER_COMMAND_ID, Duration.ofDays(365))
        );
    }

    @Override
    public StateDecision execute(
            Context context,
            Integer input,
            CommandResults commandResults,
            Persistence persistence,
            final Communication communication) {
        throw new RuntimeException("test api failing");
    }

    @Override
    public WorkflowStateOptions getStateOptions() {
        return new WorkflowStateOptions().executeApiRetryPolicy(
                new RetryPolicy()
                        .maximumAttempts(1)
                        .backoffCoefficient(2f)
        );
    }
}
