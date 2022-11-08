package io.github.cadenceoss.iwf.core.mapper;

import io.github.cadenceoss.iwf.gen.models.InterStateChannelCommand;

public class InterStateChannelCommandMapper {
    public static InterStateChannelCommand toGenerated(io.github.cadenceoss.iwf.core.command.InterStateChannelCommand signalCommand) {
        return new InterStateChannelCommand()
                .commandId(signalCommand.getCommandId())
                .channelName(signalCommand.getChannelName());
    }
}
