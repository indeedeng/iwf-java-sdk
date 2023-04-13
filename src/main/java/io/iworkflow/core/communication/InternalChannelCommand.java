package io.iworkflow.core.communication;

import io.iworkflow.core.command.BaseCommand;
import org.immutables.value.Value;

@Value.Immutable
public abstract class InternalChannelCommand implements BaseCommand {

    public abstract String getChannelName();

    public static InternalChannelCommand create(final String commandId, final String channelName) {
        return ImmutableInternalChannelCommand.builder()
                .channelName(channelName)
                .commandId(commandId)
                .build();
    }

    public static InternalChannelCommand create(String channelName) {
        return ImmutableInternalChannelCommand.builder()
                .channelName(channelName)
                .build();
    }
}