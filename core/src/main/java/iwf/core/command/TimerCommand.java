package iwf.core.command;

public final class TimerCommand extends BaseCommand {

    public TimerCommand(int firingUnixTimestampSeconds) {
        super("");
        this.firingUnixTimestampSeconds = firingUnixTimestampSeconds;

    }

    // note: commandId is needed when scheduling multiple timers in a state
    public TimerCommand(String commandId, int firingUnixTimestampSeconds) {
        super(commandId);
        this.firingUnixTimestampSeconds = firingUnixTimestampSeconds;
    }

    private int firingUnixTimestampSeconds;

    public int getFiringUnixTimestampSeconds() {
        return firingUnixTimestampSeconds;
    }
}