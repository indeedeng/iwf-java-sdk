package io.github.cadenceoss.iwf.core.mapper;

import io.github.cadenceoss.iwf.core.command.SignalStatus;
import io.github.cadenceoss.iwf.gen.models.SignalResult;

public class SignalStatusMapper {
    public static SignalStatus fromGenerated(SignalResult.SignalStatusEnum signalStatus) {
        return switch (signalStatus) {
            case WAITING -> SignalStatus.REQUESTED;
            case RECEIVED -> SignalStatus.RECEIVED;
        };
    }
}
