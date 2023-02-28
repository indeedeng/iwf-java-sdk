package io.iworkflow.core.mapper;

import io.iworkflow.core.ObjectEncoder;
import io.iworkflow.core.command.CommandResults;
import io.iworkflow.core.command.ImmutableCommandResults;

import java.util.Map;
import java.util.stream.Collectors;

public class CommandResultsMapper {
    public static CommandResults fromGenerated(
            io.iworkflow.gen.models.CommandResults commandResults,
            Map<String, Class<?>> signalNameToTypeMap,
            Map<String, Class<?>> interstateChannelNameToTypeMap,
            ObjectEncoder objectEncoder) {

        ImmutableCommandResults.Builder builder = ImmutableCommandResults.builder();
        if (commandResults == null) {
            return builder.build();
        }
        if (commandResults.getSignalResults() != null) {
            builder.allSignalCommandResults(commandResults.getSignalResults().stream()
                    .map(signalResult -> SignalResultMapper.fromGenerated(
                            signalResult,
                            signalNameToTypeMap.get(signalResult.getSignalChannelName()),
                            objectEncoder))
                    .collect(Collectors.toList()));
        }
        if (commandResults.getTimerResults() != null) {
            builder.allTimerCommandResults(commandResults.getTimerResults().stream()
                    .map(TimerResultMapper::fromGenerated)
                    .collect(Collectors.toList()));
        }
        if (commandResults.getInterStateChannelResults() != null) {
            builder.allInterStateChannelCommandResult(commandResults.getInterStateChannelResults().stream()
                    .map(result -> InterStateChannelResultMapper.fromGenerated(
                            result,
                            interstateChannelNameToTypeMap.get(result.getChannelName()),
                            objectEncoder))
                    .collect(Collectors.toList()));
        }
        if(commandResults.getStateStartApiSucceeded() != null) {
            builder.stateStartApiSucceeded(commandResults.getStateStartApiSucceeded());
        }
        return builder.build();
    }
}
