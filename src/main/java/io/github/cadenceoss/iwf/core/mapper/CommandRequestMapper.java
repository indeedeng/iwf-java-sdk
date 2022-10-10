package io.github.cadenceoss.iwf.core.mapper;

import io.github.cadenceoss.iwf.core.command.CommandRequest;
import io.github.cadenceoss.iwf.core.command.SignalCommand;

import java.util.stream.Collectors;

public class CommandRequestMapper {
    public static io.github.cadenceoss.iwf.gen.models.CommandRequest toGenerated(CommandRequest commandRequest) {
        return new io.github.cadenceoss.iwf.gen.models.CommandRequest()
                .signalCommands(commandRequest.getCommands().stream()
                        .filter(baseCommand -> baseCommand instanceof SignalCommand)
                        .map(baseCommand -> (SignalCommand) baseCommand)
                        .map(SignalCommandMapper::toGenerated)
                        .collect(Collectors.toList()))
                .deciderTriggerType(commandRequest.getDeciderTriggerType());
    }
}
