package io.iworkflow.core.communication;

import io.iworkflow.core.command.BaseCommand;
import org.immutables.value.Value;

@Value.Immutable
public abstract class SignalCommand implements BaseCommand {

    public abstract String getSignalChannelName();

    public static SignalCommand create(final String commandId, final String channelName) {
        return ImmutableSignalCommand.builder()
                .signalChannelName(channelName)
                .commandId(commandId)
                .build();
    }

    public static SignalCommand create(String signalName) {
        return ImmutableSignalCommand.builder()
                .signalChannelName(signalName)
                .build();
    }
}