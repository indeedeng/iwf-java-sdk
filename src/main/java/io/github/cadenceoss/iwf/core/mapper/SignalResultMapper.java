package io.github.cadenceoss.iwf.core.mapper;

import io.github.cadenceoss.iwf.core.ObjectEncoder;
import io.github.cadenceoss.iwf.core.command.ImmutableSignalCommandResult;
import io.github.cadenceoss.iwf.core.command.SignalCommandResult;
import io.github.cadenceoss.iwf.gen.models.SignalResult;

public class SignalResultMapper {
    public static SignalCommandResult fromGenerated(
            SignalResult signalResult,
            Class<?> signalType,
            ObjectEncoder objectEncoder) {
        return ImmutableSignalCommandResult.builder()
                .commandId(signalResult.getCommandId())
                .signalRequestStatusEnum(signalResult.getSignalRequestStatus())
                .signalChannelName(signalResult.getSignalChannelName())
                .signalValue(objectEncoder.decode(signalResult.getSignalValue(), signalType))
                .build();
    }
}
