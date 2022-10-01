package iwf.core.command;

import org.immutables.value.Value;

@Value.Immutable
public abstract class SignalCommandResult {

    public abstract String getCommandId();

    public abstract String getSignalName();

    public abstract Object getSignalValue();

    public abstract SignalStatus getSignalStatus();
}