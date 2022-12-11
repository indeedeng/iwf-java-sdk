package io.github.cadenceoss.iwf.core.communication;

import org.immutables.value.Value;

@Value.Immutable
public abstract class SignalChannel implements CommunicationMethod {

    public abstract Class getSignalValueType();

    public abstract String getSignalChannelName();

    public static SignalChannel create(Class type, String name) {
        return ImmutableSignalChannel.builder()
                .signalChannelName(name)
                .signalValueType(type)
                .build();
    }
}
