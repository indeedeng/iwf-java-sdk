package io.iworkflow.core.communication;

import io.iworkflow.core.command.BaseCommand;
import org.immutables.value.Value;

@Value.Immutable
public abstract class SignalCommand implements BaseCommand {

    public abstract String getSignalChannelName();

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