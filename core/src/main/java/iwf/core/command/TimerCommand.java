package iwf.core.command;

import org.immutables.value.Value;

@Value.Immutable
public abstract class TimerCommand implements BaseCommand {

    public abstract int getFiringUnixTimestampSeconds();
}