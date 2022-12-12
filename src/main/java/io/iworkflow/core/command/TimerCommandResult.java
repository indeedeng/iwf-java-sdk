package io.iworkflow.core.command;

import io.iworkflow.gen.models.TimerResult;
import org.immutables.value.Value;

@Value.Immutable
public abstract class TimerCommandResult {

    public abstract TimerResult.TimerStatusEnum getTimerStatus();

    public abstract String getCommandId();
}