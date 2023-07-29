package io.iworkflow.core.communication;

import io.iworkflow.core.command.BaseCommand;
import io.iworkflow.core.command.ImmutableSuperCommand;
import io.iworkflow.core.command.SuperCommand;
import org.immutables.value.Value;

@Value.Immutable
public abstract class SignalCommand implements BaseCommand {

    public abstract String getSignalChannelName();

    /**
     * Create a super command that represents one or many signal commands.
     *
     * @param commandId     required. All the signal commands created here will share the same commandId.
     * @param signalName    required.
     * @param count         required. It represents the number of commands to create.
     * @return super command
     */
    public static SuperCommand create(final String commandId, final String signalName, final int count) {
        return ImmutableSuperCommand.builder()
                .commandId(commandId)
                .name(signalName)
                .count(Math.max(1, count))
                .type(SuperCommand.Type.SIGNAL_CHANNEL)
                .build();
    }

    /**
     * Create one signal command.
     *
     * @param commandId    required.
     * @param signalName   required.
     * @return signal command
     */
    public static SignalCommand create(final String commandId, final String signalName) {
        return ImmutableSignalCommand.builder()
                .commandId(commandId)
                .signalChannelName(signalName)
                .build();
    }

    /**
     * Create a super command that represents one or many signal commands.
     *
     * @param signalName    required.
     * @param count         required. It represents the number of commands to create.
     * @return super command
     */
    public static SuperCommand create(final String signalName, final int count) {
        return ImmutableSuperCommand.builder()
                .name(signalName)
                .count(Math.max(1, count))
                .type(SuperCommand.Type.SIGNAL_CHANNEL)
                .build();
    }

    /**
     * Create one signal command.
     *
     * @param signalName     required.
     * @return signal command
     */
    public static SignalCommand create(final String signalName) {
        return ImmutableSignalCommand.builder()
                .signalChannelName(signalName)
                .build();
    }
}