package iwf.core.command;

import org.immutables.value.Value;

@Value.Immutable
public interface ActivityOptions {

    // TODO: other optional configs: 1. retryOption, 2. tasklist 3, other detailed timeouts(e.g. scheduleToStart, heartbeat)

    int getStartToCloseTimeoutSeconds();

    String getActivityCommandId();
}
