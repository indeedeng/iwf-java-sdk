package io.github.cadenceoss.iwf.core.mapper;

import io.github.cadenceoss.iwf.core.command.ImmutableSignalCommandResult;
import io.github.cadenceoss.iwf.core.command.SignalCommandResult;
import io.github.cadenceoss.iwf.gen.models.SignalResult;

public class SignalResultMapper {
    public static SignalCommandResult fromGenerated(SignalResult signalResult) {
        return ImmutableSignalCommandResult.builder()
                .commandId(signalResult.getCommandId())
                .signalStatus(SignalStatusMapper.fromGenerated(signalResult.getSignalStatus()))
                .signalName(signalResult.getSignalName())
                .signalValue(signalResult.getSignalValue())
                .build();
    }
}
