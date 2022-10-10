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
                .signalStatusEnum(signalResult.getSignalStatus())
                .signalName(signalResult.getSignalName())
                .signalValue(decode(signalResult, signalType, objectEncoder))
                .build();
    }

    private static <T> T decode(SignalResult signalResult, Class<T> signalType, ObjectEncoder objectEncoder) {
        return objectEncoder.fromData(signalResult.getSignalValue().getData(), signalType);
    }
}
