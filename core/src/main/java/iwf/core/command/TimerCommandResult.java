package iwf.core.command;

import org.immutables.value.Value;

@Value.Immutable
public abstract class TimerCommandResult {

    public abstract TimerStatus getTimerStatus();

    public abstract String getCommandId();
}