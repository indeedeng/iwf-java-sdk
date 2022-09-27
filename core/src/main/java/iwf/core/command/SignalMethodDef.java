package iwf.core.command;

import org.immutables.value.Value;

@Value.Immutable
public interface SignalMethodDef<T> {

    Class<T> getSignalType();

    String getSignalName();
}
