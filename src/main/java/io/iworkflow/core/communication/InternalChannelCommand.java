package io.iworkflow.core.communication;

import io.iworkflow.core.command.BaseCommand;
import org.immutables.value.Value;

@Value.Immutable
public abstract class InternalChannelCommand implements BaseCommand {

    public abstract String getChannelName();

    /**
     * Create one internal channel command.
     *
     * @param commandId     required.
     * @param channelName   required.
     * @return internal channel command
     */
    public static InternalChannelCommand create(final String commandId, final String channelName) {
        return ImmutableInternalChannelCommand.builder()
                .commandId(commandId)
                .channelName(channelName)
                .build();
    }

    /**
     * Create one internal channel command.
     *
     * @param channelName   required.
     * @return internal channel command
     */
    public static InternalChannelCommand create(final String channelName) {
        return ImmutableInternalChannelCommand.builder()
                .channelName(channelName)
                .build();
    }
}