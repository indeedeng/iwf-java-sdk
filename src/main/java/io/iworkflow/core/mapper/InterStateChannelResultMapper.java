package io.iworkflow.core.mapper;

import io.iworkflow.core.ObjectEncoder;
import io.iworkflow.core.communication.ImmutableInterStateChannelCommandResult;
import io.iworkflow.core.communication.InterStateChannelCommandResult;
import io.iworkflow.gen.models.InterStateChannelResult;

import java.util.Optional;

public class InterStateChannelResultMapper {
    public static InterStateChannelCommandResult fromGenerated(
            InterStateChannelResult result,
            Class<?> type,
            ObjectEncoder objectEncoder) {
        return ImmutableInterStateChannelCommandResult.builder()
                .commandId(result.getCommandId())
                .requestStatusEnum(result.getRequestStatus())
                .channelName(result.getChannelName())
                .value(Optional.ofNullable(objectEncoder.decode(result.getValue(), type)))
                .build();
    }
}
