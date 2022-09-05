package iwf.core.command;

public final class TimerCommandResult {

    private final String commandId;
    private final TimerStatus timerStatus;

    public TimerCommandResult(final String commandId, final TimerStatus timerStatus) {
        this.commandId = commandId;
        this.timerStatus = timerStatus;
    }

    public TimerStatus getTimerStatus() {
        return timerStatus;
    }

    public String getCommandId() {
        return commandId;
    }
}