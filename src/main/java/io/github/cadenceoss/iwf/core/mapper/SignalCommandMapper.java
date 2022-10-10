package io.github.cadenceoss.iwf.core.mapper;

import io.github.cadenceoss.iwf.gen.models.SignalCommand;

public class SignalCommandMapper {
    public static SignalCommand toGenerated(io.github.cadenceoss.iwf.core.command.SignalCommand signalCommand) {
        return new SignalCommand()
                .commandId(signalCommand.getCommandId())
                .signalName(signalCommand.getSignalName());
    }
}
