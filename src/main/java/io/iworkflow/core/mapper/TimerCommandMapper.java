package io.iworkflow.core.mapper;

import io.iworkflow.gen.models.TimerCommand;

public class TimerCommandMapper {
    public static TimerCommand toGenerated(io.iworkflow.core.command.TimerCommand timerCommand) {
        final TimerCommand command = new TimerCommand()
                .durationSeconds(timerCommand.getDurationSeconds());
        if (timerCommand.getCommandId().isPresent()) {
            command.commandId(timerCommand.getCommandId().get());
        }
        return command;
    }
}
