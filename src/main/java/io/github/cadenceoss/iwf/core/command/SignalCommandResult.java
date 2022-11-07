package io.github.cadenceoss.iwf.core.command;

import io.github.cadenceoss.iwf.gen.models.SignalResult;
import org.immutables.value.Value;

import java.util.Optional;

@Value.Immutable
public abstract class SignalCommandResult {

    public abstract String getCommandId();

    public abstract String getSignalChannelName();

    public abstract Optional<Object> getSignalValue();

    public abstract SignalResult.SignalRequestStatusEnum getSignalRequestStatusEnum();
}