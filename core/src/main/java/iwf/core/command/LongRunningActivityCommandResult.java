package iwf.core.command;

import org.immutables.value.Value;

@Value.Immutable
public interface LongRunningActivityCommandResult {

    String getActivityType();

    Object getOutput();

    ActivityTimeoutType getActivityTimeoutType();

    ActivityStatus getActivityStatus();

    String getActivityCommandId();
}