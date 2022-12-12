package io.iworkflow.core.communication;

import org.immutables.value.Value;

@Value.Immutable
public abstract class InterStateChannelDef implements CommunicationMethodDef {

    public abstract Class getValueType();

    public abstract String getChannelName();

    public static InterStateChannelDef create(Class type, String name) {
        return ImmutableInterStateChannelDef.builder()
                .channelName(name)
                .valueType(type)
                .build();
    }
}
