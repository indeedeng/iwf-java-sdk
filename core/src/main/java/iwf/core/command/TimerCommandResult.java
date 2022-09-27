package iwf.core.command;

import org.immutables.value.Value;

@Value.Immutable
public interface TimerCommandResult {

    TimerStatus getTimerStatus();

    String getCommandId();
}