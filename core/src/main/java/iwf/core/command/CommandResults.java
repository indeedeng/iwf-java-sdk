package iwf.core.command;

import java.util.List;

/**
 * This is the container of all requested commands' results/statuses
 */
public class CommandResults {

    public List<LongRunningActivityCommandResult> getAllLongRunningActivityCommandResults() {
        return null;
    }

    public List<SignalCommandResult> getAllSignalCommandResults() {
        return null;
    }

    public List<TimerCommandResult> getAllTimerCommandResults() {
        return null;
    }

    public <T> T getActivityOutputByIndex(int idx) {
        return null;
    }

    public <T> T getActivityOutputById(String commandId) {
        return null;
    }

    public LongRunningActivityCommandResult getActivityCommandResultByIndex(int idx) {
        return null;
    }

    public LongRunningActivityCommandResult getActivityCommandResultById(String commandId) {
        return null;
    }

    public <T> T getSignalValueByIndex(int idx) {
        return null;
    }

    public <T> T getSignalValueById(String commandId) {
        return null;
    }

}
