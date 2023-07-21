package io.iworkflow.integ.signal;

import io.iworkflow.core.Context;
import io.iworkflow.core.StateDecision;
import io.iworkflow.core.WorkflowState;
import io.iworkflow.core.command.CommandRequest;
import io.iworkflow.core.command.CommandResults;
import io.iworkflow.core.command.TimerCommand;
import io.iworkflow.core.command.TimerCommandResult;
import io.iworkflow.core.communication.Communication;
import io.iworkflow.core.communication.SignalCommand;
import io.iworkflow.core.communication.SignalCommandResult;
import io.iworkflow.core.persistence.Persistence;
import io.iworkflow.gen.models.ChannelRequestStatus;
import io.iworkflow.gen.models.TimerStatus;

import java.time.Duration;
import java.util.Arrays;

import static io.iworkflow.integ.signal.BasicSignalWorkflow.SIGNAL_CHANNEL_NAME_1;
import static io.iworkflow.integ.signal.BasicSignalWorkflow.SIGNAL_CHANNEL_NAME_2;
import static io.iworkflow.integ.signal.BasicSignalWorkflow.SIGNAL_CHANNEL_NAME_3;
import static io.iworkflow.integ.signal.BasicSignalWorkflow.SIGNAL_CHANNEL_PREFIX_1;

public class BasicSignalWorkflowState2 implements WorkflowState<Integer> {
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
                Arrays.asList(
                        Arrays.asList(SIGNAL_COMMAND_ID_1, SIGNAL_COMMAND_ID_3, TIMER_COMMAND_ID)
                ),
                SignalCommand.create(SIGNAL_COMMAND_ID_1, SIGNAL_CHANNEL_NAME_1),
                SignalCommand.create(SIGNAL_COMMAND_ID_1, SIGNAL_CHANNEL_NAME_2),
                SignalCommand.create(SIGNAL_COMMAND_ID_2, SIGNAL_CHANNEL_NAME_3),
                SignalCommand.create(SIGNAL_COMMAND_ID_3, SIGNAL_CHANNEL_PREFIX_1 + "1"),
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
        SignalCommandResult signalCommandResult = commandResults.getAllSignalCommandResults().get(0);
        Integer output = input + (Integer) signalCommandResult.getSignalValue().get();

        SignalCommandResult signalCommandResult2 = commandResults.getAllSignalCommandResults().get(1);
        if (signalCommandResult2.getSignalRequestStatusEnum() != ChannelRequestStatus.WAITING) {
            throw new RuntimeException("the second signal should be waiting");
        }

        SignalCommandResult signalCommandResult3 = commandResults.getAllSignalCommandResults().get(2);
        if (signalCommandResult3.getSignalRequestStatusEnum() != ChannelRequestStatus.RECEIVED || !signalCommandResult3.getCommandId().equals(SIGNAL_COMMAND_ID_2)) {
            throw new RuntimeException("the 3 signal should be received");
        }

        SignalCommandResult signalCommandResult4 = commandResults.getAllSignalCommandResults().get(3);
        if (signalCommandResult4.getSignalRequestStatusEnum() != ChannelRequestStatus.RECEIVED || !signalCommandResult4.getCommandId().equals(SIGNAL_COMMAND_ID_3)) {
            throw new RuntimeException("the 4 signal created by prefix should be received");
        }

        final TimerCommandResult timerResult = commandResults.getAllTimerCommandResults().get(0);
        if (timerResult.getTimerStatus() != TimerStatus.FIRED) {
            throw new RuntimeException("the timer should be fired");
        }
        return StateDecision.gracefulCompleteWorkflow(output);
    }
}
