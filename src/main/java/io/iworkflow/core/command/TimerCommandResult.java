package io.iworkflow.core.command;

import io.iworkflow.gen.models.TimerStatus;
import org.immutables.value.Value;

@Value.Immutable
public abstract class TimerCommandResult {

    public abstract TimerStatus getTimerStatus();

    public abstract String getCommandId();
}