package iwf.core.command;

public final class SignalMethodDef<T> {
    private String signalName;
    private Class<T> signalType;

    public SignalMethodDef(final String signalName, final Class<T> signalType) {
        this.signalName = signalName;
        this.signalType = signalType;
    }

    public Class<T> getSignalType() {
        return signalType;
    }

    public String getSignalName() {
        return signalName;
    }
}
