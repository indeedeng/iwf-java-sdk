package io.github.cadenceoss.iwf.core.command;

import org.immutables.value.Value;

import java.util.List;

/**
 * This is the container of all requested commands' results/statuses
 */
@Value.Immutable
public abstract class CommandResults {

    public abstract List<LongRunningActivityCommandResult> getAllLongRunningActivityCommandResults();

    public abstract List<SignalCommandResult> getAllSignalCommandResults();

    public abstract List<TimerCommandResult> getAllTimerCommandResults();

    // below are helpers
    public <T> T getActivityOutputByIndex(int idx) {
        throw new RuntimeException("TODO");
    }

    public <T> T getActivityOutputById(String commandId) {
        throw new RuntimeException("TODO");
    }

    public LongRunningActivityCommandResult getActivityCommandResultByIndex(int idx) {
        throw new RuntimeException("TODO");
    }

    public LongRunningActivityCommandResult getActivityCommandResultById(String commandId) {
        throw new RuntimeException("TODO");
    }

    public <T> T getSignalValueByIndex(int idx) {
        throw new RuntimeException("TODO");
    }

    public <T> T getSignalValueById(String commandId) {
        throw new RuntimeException("TODO");
    }
}
