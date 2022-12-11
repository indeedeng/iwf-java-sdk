package io.github.cadenceoss.iwf.core.mapper;

import io.github.cadenceoss.iwf.core.ObjectEncoder;
import io.github.cadenceoss.iwf.core.communication.ImmutableSignalCommandResult;
import io.github.cadenceoss.iwf.core.communication.SignalCommandResult;
import io.github.cadenceoss.iwf.gen.models.SignalResult;

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
