package io.github.cadenceoss.iwf.core.mapper;

import io.github.cadenceoss.iwf.gen.models.SignalCommand;

public class SignalCommandMapper {
    public static SignalCommand toGenerated(io.github.cadenceoss.iwf.core.communication.SignalCommand signalCommand) {
        final SignalCommand command = new SignalCommand()
                .signalChannelName(signalCommand.getSignalChannelName());
        if (signalCommand.getCommandId().isPresent()) {
            command.commandId(signalCommand.getCommandId().get());
        }
        return command;
    }
}
