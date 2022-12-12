package io.iworkflow.core.mapper;

import io.iworkflow.core.command.ImmutableTimerCommandResult;
import io.iworkflow.core.command.TimerCommandResult;
import io.iworkflow.gen.models.TimerResult;

public class TimerResultMapper {
    public static TimerCommandResult fromGenerated(
            TimerResult timerResult) {
        return ImmutableTimerCommandResult.builder()
                .commandId(timerResult.getCommandId())
                .timerStatus(timerResult.getTimerStatus())
                .build();
    }
}
