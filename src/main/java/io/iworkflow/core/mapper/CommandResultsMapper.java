package io.iworkflow.core.mapper;

import io.iworkflow.core.ObjectEncoder;
import io.iworkflow.core.TypeStore;
import io.iworkflow.core.command.CommandResults;
import io.iworkflow.core.command.ImmutableCommandResults;

import java.util.stream.Collectors;

public class CommandResultsMapper {
    public static CommandResults fromGenerated(
            final io.iworkflow.gen.models.CommandResults commandResults,
            final TypeStore signalChannelTypeStore,
            final TypeStore internalChannelTypeStore,
            final ObjectEncoder objectEncoder) {

        ImmutableCommandResults.Builder builder = ImmutableCommandResults.builder();
        if (commandResults == null) {
            return builder.build();
        }
        if (commandResults.getSignalResults() != null) {
            builder.allSignalCommandResults(commandResults.getSignalResults().stream()
                    .map(signalResult -> SignalResultMapper.fromGenerated(
                            signalResult,
                            signalChannelTypeStore.getType(signalResult.getSignalChannelName()),
                            objectEncoder))
                    .collect(Collectors.toList()));
        }
        if (commandResults.getTimerResults() != null) {
            builder.allTimerCommandResults(commandResults.getTimerResults().stream()
                    .map(TimerResultMapper::fromGenerated)
                    .collect(Collectors.toList()));
        }
        if (commandResults.getInterStateChannelResults() != null) {
            builder.allInternalChannelCommandResult(commandResults.getInterStateChannelResults().stream()
                    .map(result -> InternalChannelResultMapper.fromGenerated(
                            result,
                            internalChannelTypeStore.getType(result.getChannelName()),
                            objectEncoder))
                    .collect(Collectors.toList()));
        }
        if(commandResults.getStateStartApiSucceeded() != null) {
            builder.waitUntilApiSucceeded(commandResults.getStateStartApiSucceeded());
        }
        return builder.build();
    }
}
