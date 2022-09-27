package iwf.core.command;

import org.immutables.value.Value;

@Value.Immutable
public interface SignalCommandResult {

    String getCommandId();

    String getSignalName();

    Object getSignalValue();

    SignalStatus getSignalStatus();
}