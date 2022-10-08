package io.github.cadenceoss.iwf.core.command;

import org.immutables.value.Value;

@Value.Immutable
public abstract class LongRunningActivityDef<O> {

    public abstract String getActivityType();

    public abstract Class<O> getOutputType();
}
