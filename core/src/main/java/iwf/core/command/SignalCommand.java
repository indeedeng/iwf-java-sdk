package iwf.core.command;

import org.immutables.value.Value;

@Value.Immutable
public interface SignalCommand extends BaseCommand {

    String getSignalName();
}