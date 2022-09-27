package iwf.core.command;

import org.immutables.value.Value;

@Value.Immutable
public interface CommandCarryOverPolicy {

    CommandCarryOverType getCommandCarryOverType();

    CommandCarryOverPolicy none = ImmutableCommandCarryOverPolicy.builder().commandCarryOverType(CommandCarryOverType.NONE).build();
}
