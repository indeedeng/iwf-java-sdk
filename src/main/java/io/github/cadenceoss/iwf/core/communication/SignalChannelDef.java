package io.github.cadenceoss.iwf.core.communication;

import org.immutables.value.Value;

@Value.Immutable
public abstract class SignalChannelDef implements CommunicationMethodDef {

    public abstract Class getSignalValueType();

    public abstract String getSignalChannelName();

    public static SignalChannelDef create(Class type, String name) {
        return ImmutableSignalChannelDef.builder()
                .signalChannelName(name)
                .signalValueType(type)
                .build();
    }
}
