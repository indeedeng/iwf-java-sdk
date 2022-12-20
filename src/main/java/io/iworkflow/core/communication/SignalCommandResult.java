package io.iworkflow.core.communication;

import io.iworkflow.gen.models.SignalRequestStatus;
import org.immutables.value.Value;

import java.util.Optional;

@Value.Immutable
public abstract class SignalCommandResult {

    public abstract String getCommandId();

    public abstract String getSignalChannelName();

    public abstract Optional<Object> getSignalValue();

    public abstract SignalRequestStatus getSignalRequestStatusEnum();
}