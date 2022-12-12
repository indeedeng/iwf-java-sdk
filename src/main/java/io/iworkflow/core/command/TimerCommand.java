package io.iworkflow.core.command;

import org.immutables.value.Value;

import java.time.Duration;

@Value.Immutable
public abstract class TimerCommand implements BaseCommand {

    public abstract int getFiringUnixTimestampSeconds();

    public static TimerCommand createByDuration(String commandId, Duration duration) {
        return ImmutableTimerCommand.builder()
                .commandId(commandId)
                .firingUnixTimestampSeconds((int) (System.currentTimeMillis() / 1000 + duration.getSeconds()))
                .build();
    }

    public static TimerCommand createByDuration(Duration duration) {
        return ImmutableTimerCommand.builder()
                .firingUnixTimestampSeconds((int) (System.currentTimeMillis() / 1000 + duration.getSeconds()))
                .build();
    }
}