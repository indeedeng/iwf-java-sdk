package iwf.core.command;

import org.immutables.value.Value;

@Value.Immutable
public abstract class SignalMethodDef<T> {

    public abstract Class<T> getSignalType();

    public abstract String getSignalName();
}
