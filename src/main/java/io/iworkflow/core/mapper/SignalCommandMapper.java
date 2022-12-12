package io.iworkflow.core.mapper;

import io.iworkflow.gen.models.SignalCommand;

public class SignalCommandMapper {
    public static SignalCommand toGenerated(io.iworkflow.core.communication.SignalCommand signalCommand) {
        final SignalCommand command = new SignalCommand()
                .signalChannelName(signalCommand.getSignalChannelName());
        if (signalCommand.getCommandId().isPresent()) {
            command.commandId(signalCommand.getCommandId().get());
        }
        return command;
    }
}
