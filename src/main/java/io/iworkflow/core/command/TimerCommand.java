package io.iworkflow.core.command;

import org.immutables.value.Value;

import java.time.Duration;

@Value.Immutable
public abstract class TimerCommand implements BaseCommand {

    public abstract long getDurationSeconds();

    public static TimerCommand createByDuration(String commandId, Duration duration) {
        return ImmutableTimerCommand.builder()
                .commandId(commandId)
                .durationSeconds(duration.getSeconds())
                .build();
    }

    public static TimerCommand createByDuration(Duration duration) {
        return ImmutableTimerCommand.builder()
                .durationSeconds(duration.getSeconds())
                .build();
    }
}