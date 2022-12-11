package io.github.cadenceoss.iwf.core.mapper;

import io.github.cadenceoss.iwf.gen.models.InterStateChannelCommand;

public class InterStateChannelCommandMapper {
    public static InterStateChannelCommand toGenerated(io.github.cadenceoss.iwf.core.communication.InterStateChannelCommand stateChannelCommand) {
        final InterStateChannelCommand command = new InterStateChannelCommand()
                .channelName(stateChannelCommand.getChannelName());
        if (stateChannelCommand.getCommandId().isPresent()) {
            command.commandId(stateChannelCommand.getCommandId().get());
        }
        return command;
    }
}
