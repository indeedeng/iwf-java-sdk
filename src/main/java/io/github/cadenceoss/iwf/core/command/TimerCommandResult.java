package io.github.cadenceoss.iwf.core.command;

import io.github.cadenceoss.iwf.gen.models.TimerResult;
import org.immutables.value.Value;

@Value.Immutable
public abstract class TimerCommandResult {

    public abstract TimerResult.TimerStatusEnum getTimerStatus();

    public abstract String getCommandId();
}