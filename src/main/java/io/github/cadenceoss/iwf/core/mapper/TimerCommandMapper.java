package io.github.cadenceoss.iwf.core.mapper;

import io.github.cadenceoss.iwf.gen.models.TimerCommand;

public class TimerCommandMapper {
    public static TimerCommand toGenerated(io.github.cadenceoss.iwf.core.command.TimerCommand timerCommand) {
        final TimerCommand command = new TimerCommand()
                .firingUnixTimestampSeconds((long) timerCommand.getFiringUnixTimestampSeconds());
        if (timerCommand.getCommandId().isPresent()) {
            command.commandId(timerCommand.getCommandId().get());
        }
        return command;
    }
}
