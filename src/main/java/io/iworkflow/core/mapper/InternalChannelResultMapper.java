package io.iworkflow.core.mapper;

import io.iworkflow.core.ObjectEncoder;
import io.iworkflow.core.communication.ImmutableInternalChannelCommandResult;
import io.iworkflow.core.communication.InternalChannelCommandResult;
import io.iworkflow.gen.models.InterStateChannelResult;

import java.util.Optional;

public class InternalChannelResultMapper {
    public static InternalChannelCommandResult fromGenerated(
            InterStateChannelResult result,
            Class<?> type,
            ObjectEncoder objectEncoder) {
        return ImmutableInternalChannelCommandResult.builder()
                .commandId(result.getCommandId())
                .requestStatusEnum(result.getRequestStatus())
                .channelName(result.getChannelName())
                .value(Optional.ofNullable(objectEncoder.decode(result.getValue(), type)))
                .build();
    }
}
