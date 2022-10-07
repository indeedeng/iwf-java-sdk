package iwf.core.command;

import org.immutables.value.Value;

@Value.Immutable
public abstract class SignalMethodDef<T> {

    public abstract Class<T> getSignalType();

    public abstract String getSignalName();

    public static SignalMethodDef create(Class signalType, String signalName) {
        return ImmutableSignalMethodDef.builder()
                .signalName(signalName)
                .signalType(signalType)
                .build();
    }
}
