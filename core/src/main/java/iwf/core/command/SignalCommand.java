package iwf.core.command;

import org.immutables.value.Value;

@Value.Immutable
public abstract class SignalCommand implements BaseCommand {

    public abstract String getSignalName();

    public static SignalCommand create(final String commandId, final String signalName) {
        return ImmutableSignalCommand.builder()
                .signalName(signalName)
                .commandId(commandId)
                .build();
    }

    public static SignalCommand create(String signalName) {
        return ImmutableSignalCommand.builder()
                .signalName(signalName)
                .build();
    }
}