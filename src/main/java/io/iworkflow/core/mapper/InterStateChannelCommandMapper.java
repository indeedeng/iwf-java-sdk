package io.iworkflow.core.mapper;

import io.iworkflow.gen.models.InterStateChannelCommand;

public class InterStateChannelCommandMapper {
    public static InterStateChannelCommand toGenerated(io.iworkflow.core.communication.InterStateChannelCommand stateChannelCommand) {
        final InterStateChannelCommand command = new InterStateChannelCommand()
                .channelName(stateChannelCommand.getChannelName());
        if (stateChannelCommand.getCommandId().isPresent()) {
            command.commandId(stateChannelCommand.getCommandId().get());
        }
        return command;
    }
}
