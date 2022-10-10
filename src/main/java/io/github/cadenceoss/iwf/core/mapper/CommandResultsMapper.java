package io.github.cadenceoss.iwf.core.mapper;

import io.github.cadenceoss.iwf.core.command.CommandResults;
import io.github.cadenceoss.iwf.core.command.ImmutableCommandResults;

import java.util.stream.Collectors;

public class CommandResultsMapper {
    public static CommandResults fromGenerated(io.github.cadenceoss.iwf.gen.models.CommandResults commandResults) {
        ImmutableCommandResults.Builder builder = ImmutableCommandResults.builder();
        if (commandResults == null) {
            return builder.build();
        }
        if (commandResults.getSignalResults() != null) {
            builder.allSignalCommandResults(commandResults.getSignalResults().stream()
                    .map(SignalResultMapper::fromGenerated)
                    .collect(Collectors.toList()));
        }
        // TODO: add mapping for activity results and timer results
        return builder.build();
    }
}
