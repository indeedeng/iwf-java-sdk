package io.iworkflow.core.mapper;

import io.iworkflow.core.ObjectEncoder;
import io.iworkflow.core.TypeMapsStore;
import io.iworkflow.core.command.CommandResults;
import io.iworkflow.core.command.ImmutableCommandResults;
import io.iworkflow.core.communication.ChannelType;
import io.iworkflow.core.utils.ChannelUtils;

import java.util.stream.Collectors;

public class CommandResultsMapper {
    public static CommandResults fromGenerated(
            final io.iworkflow.gen.models.CommandResults commandResults,
            final TypeMapsStore signalChannelTypeMapsStore,
            final TypeMapsStore internalChannelTypeMapsStore,
            final ObjectEncoder objectEncoder) {

        ImmutableCommandResults.Builder builder = ImmutableCommandResults.builder();
        if (commandResults == null) {
            return builder.build();
        }
        if (commandResults.getSignalResults() != null) {
            builder.allSignalCommandResults(commandResults.getSignalResults().stream()
                    .map(signalResult -> SignalResultMapper.fromGenerated(
                            signalResult,
                            ChannelUtils.getChannelType(
                                    signalResult.getSignalChannelName(),
                                    ChannelType.SIGNAL,
                                    signalChannelTypeMapsStore
                            ),
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
                            ChannelUtils.getChannelType(
                                    result.getChannelName(),
                                    ChannelType.INTERNAL,
                                    internalChannelTypeMapsStore
                            ),
                            objectEncoder))
                    .collect(Collectors.toList()));
        }
        if(commandResults.getStateStartApiSucceeded() != null) {
            builder.waitUntilApiSucceeded(commandResults.getStateStartApiSucceeded());
        }
        return builder.build();
    }
}
