package io.github.cadenceoss.iwf.core.communication;

import io.github.cadenceoss.iwf.gen.models.InterStateChannelResult;
import org.immutables.value.Value;

import java.util.Optional;

@Value.Immutable
public abstract class InterStateChannelCommandResult {

    public abstract String getCommandId();

    public abstract String getChannelName();

    public abstract Optional<Object> getValue();

    public abstract InterStateChannelResult.RequestStatusEnum getRequestStatusEnum();
}