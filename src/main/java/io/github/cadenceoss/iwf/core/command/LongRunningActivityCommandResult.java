package io.github.cadenceoss.iwf.core.command;

import org.immutables.value.Value;

@Value.Immutable
public abstract class LongRunningActivityCommandResult {

    public abstract String getActivityType();

    public abstract Object getOutput();

    public abstract ActivityTimeoutType getActivityTimeoutType();

    public abstract ActivityStatus getActivityStatus();

    public abstract String getActivityCommandId();
}