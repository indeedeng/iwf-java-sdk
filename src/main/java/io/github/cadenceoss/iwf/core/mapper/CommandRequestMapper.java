package io.github.cadenceoss.iwf.core.mapper;

import io.github.cadenceoss.iwf.core.command.CommandRequest;
import io.github.cadenceoss.iwf.core.communication.InterStateChannelCommand;
import io.github.cadenceoss.iwf.core.communication.SignalCommand;
import io.github.cadenceoss.iwf.core.command.TimerCommand;

import java.util.stream.Collectors;

public class CommandRequestMapper {
    public static io.github.cadenceoss.iwf.gen.models.CommandRequest toGenerated(CommandRequest commandRequest) {
        return new io.github.cadenceoss.iwf.gen.models.CommandRequest()
                .signalCommands(
                        commandRequest.getCommands().stream()
                                .filter(baseCommand -> baseCommand instanceof SignalCommand)
                                .map(baseCommand -> (SignalCommand) baseCommand)
                                .map(SignalCommandMapper::toGenerated)
                                .collect(Collectors.toList())
                ).timerCommands(
                        commandRequest.getCommands().stream()
                                .filter(baseCommand -> baseCommand instanceof TimerCommand)
                                .map(baseCommand -> (TimerCommand) baseCommand)
                                .map(TimerCommandMapper::toGenerated)
                                .collect(Collectors.toList())
                )
                .interStateChannelCommands(
                        commandRequest.getCommands().stream()
                                .filter(baseCommand -> baseCommand instanceof InterStateChannelCommand)
                                .map(baseCommand -> (InterStateChannelCommand) baseCommand)
                                .map(InterStateChannelCommandMapper::toGenerated)
                                .collect(Collectors.toList())
                )
                .deciderTriggerType(commandRequest.getDeciderTriggerType());
    }
}
