package io.iworkflow.core.communication;

import io.iworkflow.core.command.BaseCommand;
import io.iworkflow.core.command.ImmutableSuperCommand;
import io.iworkflow.core.command.SuperCommand;
import org.immutables.value.Value;

@Value.Immutable
public abstract class InternalChannelCommand implements BaseCommand {

    public abstract String getChannelName();

    /**
     * Create a super command that represents one or many internal channel commands.
     *
     * @param commandId     required. All the internal channel commands created here will share the same commandId.
     * @param channelName   required.
     * @param count         required. It represents the number of internal channel commands to create.
     * @return super command
     */
    public static SuperCommand create(final String commandId, final String channelName, final int count) {
        return ImmutableSuperCommand.builder()
                .commandId(commandId)
                .name(channelName)
                .count(Math.max(1, count))
                .type(SuperCommand.Type.INTERNAL_CHANNEL)
                .build();
    }

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
     * Create a super command that represents one or many internal channel commands.
     *
     * @param channelName   required.
     * @param count         required. It represents the number of commands to create.
     * @return super command
     */
    public static SuperCommand create(final String channelName, final int count) {
        return ImmutableSuperCommand.builder()
                .name(channelName)
                .count(Math.max(1, count))
                .type(SuperCommand.Type.INTERNAL_CHANNEL)
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