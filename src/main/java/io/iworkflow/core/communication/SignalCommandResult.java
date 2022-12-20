package io.iworkflow.core.communication;

import io.iworkflow.gen.models.ChannelRequestStatus;
import org.immutables.value.Value;

import java.util.Optional;

@Value.Immutable
public abstract class SignalCommandResult {

    public abstract String getCommandId();

    public abstract String getSignalChannelName();

    public abstract Optional<Object> getSignalValue();

    public abstract ChannelRequestStatus getSignalRequestStatusEnum();
}