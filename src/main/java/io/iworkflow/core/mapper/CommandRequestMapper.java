package io.iworkflow.core.mapper;

import io.iworkflow.core.command.CommandRequest;
import io.iworkflow.core.command.TimerCommand;
import io.iworkflow.core.communication.InterStateChannelCommand;
import io.iworkflow.core.communication.SignalCommand;

import java.util.List;
import java.util.stream.Collectors;

public class CommandRequestMapper {
    public static io.iworkflow.gen.models.CommandRequest toGenerated(CommandRequest commandRequest) {

        final List<io.iworkflow.gen.models.SignalCommand> signalCommands = commandRequest.getCommands().stream()
                .filter(baseCommand -> baseCommand instanceof SignalCommand)
                .map(baseCommand -> (SignalCommand) baseCommand)
                .map(SignalCommandMapper::toGenerated)
                .collect(Collectors.toList());

        final List<io.iworkflow.gen.models.TimerCommand> timerCommands = commandRequest.getCommands().stream()
                .filter(baseCommand -> baseCommand instanceof TimerCommand)
                .map(baseCommand -> (TimerCommand) baseCommand)
                .map(TimerCommandMapper::toGenerated)
                .collect(Collectors.toList());

        final List<io.iworkflow.gen.models.InterStateChannelCommand> interstateChannelCommands = commandRequest.getCommands().stream()
                .filter(baseCommand -> baseCommand instanceof InterStateChannelCommand)
                .map(baseCommand -> (InterStateChannelCommand) baseCommand)
                .map(InterStateChannelCommandMapper::toGenerated)
                .collect(Collectors.toList());

        final io.iworkflow.gen.models.CommandRequest commandRequestResults = new io.iworkflow.gen.models.CommandRequest()
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
