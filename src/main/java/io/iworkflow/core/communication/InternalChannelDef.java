package io.iworkflow.core.communication;

import org.immutables.value.Value;

@Value.Immutable
public abstract class InternalChannelDef implements CommunicationMethodDef {

    public abstract Class getValueType();

    public abstract String getChannelName();

    public static InternalChannelDef create(Class type, String name) {
        return ImmutableInternalChannelDef.builder()
                .channelName(name)
                .valueType(type)
                .build();
    }
}
