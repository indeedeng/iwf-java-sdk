package io.github.cadenceoss.iwf.core.mapper;

import io.github.cadenceoss.iwf.gen.models.TimerCommand;

public class TimerCommandMapper {
    public static TimerCommand toGenerated(io.github.cadenceoss.iwf.core.command.TimerCommand timerCommand) {
        return new TimerCommand()
                .commandId(timerCommand.getCommandId())
                .firingUnixTimestampSeconds((long) timerCommand.getFiringUnixTimestampSeconds());
    }
}
