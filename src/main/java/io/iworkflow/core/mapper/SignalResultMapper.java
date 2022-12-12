package io.iworkflow.core.mapper;

import io.iworkflow.core.ObjectEncoder;
import io.iworkflow.core.communication.ImmutableSignalCommandResult;
import io.iworkflow.core.communication.SignalCommandResult;
import io.iworkflow.gen.models.SignalResult;

import java.util.Optional;

public class SignalResultMapper {
    public static SignalCommandResult fromGenerated(
            SignalResult signalResult,
            Class<?> signalType,
            ObjectEncoder objectEncoder) {
        return ImmutableSignalCommandResult.builder()
                .commandId(signalResult.getCommandId())
                .signalRequestStatusEnum(signalResult.getSignalRequestStatus())
                .signalChannelName(signalResult.getSignalChannelName())
                .signalValue(Optional.ofNullable(objectEncoder.decode(signalResult.getSignalValue(), signalType)))
                .build();
    }
}
