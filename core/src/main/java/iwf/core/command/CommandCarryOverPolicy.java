package iwf.core.command;

import org.immutables.value.Value;

@Value.Immutable
public abstract class CommandCarryOverPolicy {

    public abstract CommandCarryOverType getCommandCarryOverType();

    public static final CommandCarryOverPolicy none = ImmutableCommandCarryOverPolicy.builder().commandCarryOverType(CommandCarryOverType.NONE).build();
}
