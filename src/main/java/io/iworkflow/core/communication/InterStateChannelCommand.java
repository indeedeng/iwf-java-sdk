package io.iworkflow.core.communication;

import io.iworkflow.core.command.BaseCommand;
import org.immutables.value.Value;

@Value.Immutable
public abstract class InterStateChannelCommand implements BaseCommand {

    public abstract String getChannelName();

    public static InterStateChannelCommand create(final String commandId, final String channelName) {
        return ImmutableInterStateChannelCommand.builder()
                .channelName(channelName)
                .commandId(commandId)
                .build();
    }

    public static InterStateChannelCommand create(String signalName) {
        return ImmutableInterStateChannelCommand.builder()
                .channelName(signalName)
                .build();
    }
}