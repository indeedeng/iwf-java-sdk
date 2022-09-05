package iwf.core.command;

public final class SignalCommandResult {
    private final String commandId;
    private final String signalName;
    private final Object signalValue;
    private final SignalStatus signalStatus;

    public SignalCommandResult(final String commandId, final String signalName, final Object signalValue, final SignalStatus signalStatus) {
        this.commandId = commandId;
        this.signalName = signalName;
        this.signalValue = signalValue;
        this.signalStatus = signalStatus;
    }

    public String getCommandId() {
        return commandId;
    }

    public String getSignalName() {
        return signalName;
    }

    public Object getSignalValue() {
        return signalValue;
    }

    public SignalStatus getSignalStatus() {
        return signalStatus;
    }
}