package io.github.cadenceoss.iwf.core.mapper;

import io.github.cadenceoss.iwf.core.command.ImmutableTimerCommandResult;
import io.github.cadenceoss.iwf.core.command.TimerCommandResult;
import io.github.cadenceoss.iwf.gen.models.TimerResult;

public class TimerResultMapper {
    public static TimerCommandResult fromGenerated(
            TimerResult timerResult) {
        return ImmutableTimerCommandResult.builder()
                .commandId(timerResult.getCommandId())
                .timerStatus(timerResult.getTimerStatus())
                .build();
    }
}
