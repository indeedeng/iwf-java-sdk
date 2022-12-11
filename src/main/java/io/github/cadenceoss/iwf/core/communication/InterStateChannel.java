package io.github.cadenceoss.iwf.core.communication;

import io.github.cadenceoss.iwf.core.command.ImmutableInterStateChannel;
import org.immutables.value.Value;

@Value.Immutable
public abstract class InterStateChannel implements CommunicationMethod {

    public abstract Class getValueType();

    public abstract String getChannelName();

    public static InterStateChannel create(Class type, String name) {
        return ImmutableInterStateChannel.builder()
                .channelName(name)
                .valueType(type)
                .build();
    }
}
