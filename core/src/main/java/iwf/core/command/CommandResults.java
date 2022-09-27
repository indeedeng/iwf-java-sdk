package iwf.core.command;

import org.immutables.value.Value;

import java.util.List;

/**
 * This is the container of all requested commands' results/statuses
 */
@Value.Immutable
public interface CommandResults {

    List<LongRunningActivityCommandResult> getAllLongRunningActivityCommandResults();

    List<SignalCommandResult> getAllSignalCommandResults();

    List<TimerCommandResult> getAllTimerCommandResults();

    class helper {
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

}
