package io.github.cadenceoss.iwf.core.mapper;

import io.github.cadenceoss.iwf.core.ObjectEncoder;
import io.github.cadenceoss.iwf.core.command.ImmutableInterStateChannelCommandResult;
import io.github.cadenceoss.iwf.core.command.InterStateChannelCommandResult;
import io.github.cadenceoss.iwf.gen.models.InterStateChannelResult;

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
