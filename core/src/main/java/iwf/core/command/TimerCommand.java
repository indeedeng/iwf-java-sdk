package iwf.core.command;

import org.immutables.value.Value;

@Value.Immutable
public interface TimerCommand extends BaseCommand {

    int getFiringUnixTimestampSeconds();
}