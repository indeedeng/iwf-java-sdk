package io.github.cadenceoss.iwf.core.mapper;

import io.github.cadenceoss.iwf.core.command.CommandRequest;
import io.github.cadenceoss.iwf.core.command.TimerCommand;
import io.github.cadenceoss.iwf.core.communication.InterStateChannelCommand;
import io.github.cadenceoss.iwf.core.communication.SignalCommand;

import java.util.List;
import java.util.stream.Collectors;

public class CommandRequestMapper {
    public static io.github.cadenceoss.iwf.gen.models.CommandRequest toGenerated(CommandRequest commandRequest) {

        final List<io.github.cadenceoss.iwf.gen.models.SignalCommand> signalCommands = commandRequest.getCommands().stream()
                .filter(baseCommand -> baseCommand instanceof SignalCommand)
                .map(baseCommand -> (SignalCommand) baseCommand)
                .map(SignalCommandMapper::toGenerated)
                .collect(Collectors.toList());

        final List<io.github.cadenceoss.iwf.gen.models.TimerCommand> timerCommands = commandRequest.getCommands().stream()
                .filter(baseCommand -> baseCommand instanceof TimerCommand)
                .map(baseCommand -> (TimerCommand) baseCommand)
                .map(TimerCommandMapper::toGenerated)
                .collect(Collectors.toList());

        final List<io.github.cadenceoss.iwf.gen.models.InterStateChannelCommand> interstateChannelCommands = commandRequest.getCommands().stream()
                .filter(baseCommand -> baseCommand instanceof InterStateChannelCommand)
                .map(baseCommand -> (InterStateChannelCommand) baseCommand)
                .map(InterStateChannelCommandMapper::toGenerated)
                .collect(Collectors.toList());

        final io.github.cadenceoss.iwf.gen.models.CommandRequest commandRequestResults = new io.github.cadenceoss.iwf.gen.models.CommandRequest()
                .deciderTriggerType(commandRequest.getDeciderTriggerType());
        if (signalCommands.size() > 0) {
            commandRequestResults.signalCommands(signalCommands);
        }
        if (timerCommands.size() > 0) {
            commandRequestResults.timerCommands(timerCommands);
        }
        if (interstateChannelCommands.size() > 0) {
            commandRequestResults.interStateChannelCommands(interstateChannelCommands);
        }
        return commandRequestResults;
    }
}
