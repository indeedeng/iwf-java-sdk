package io.github.cadenceoss.iwf.core.command;

import org.immutables.value.Value;

@Value.Immutable
public abstract class ActivityOptions {

    // TODO: other optional configs: 1. retryOption, 2. tasklist 3, other detailed timeouts(e.g. scheduleToStart, heartbeat)

    public abstract int getStartToCloseTimeoutSeconds();

    public abstract String getActivityCommandId();
}
