package iwf.core.command;

public final class SignalCommand extends BaseCommand {

    public SignalCommand(final String signalName) {
        super("");
        this.signalName = signalName;
    }

    public SignalCommand(final String commandId, final String signalName) {
        super(commandId);
        this.signalName = signalName;
    }

    private final String signalName;

    public String getSignalName() {
        return signalName;
    }

}