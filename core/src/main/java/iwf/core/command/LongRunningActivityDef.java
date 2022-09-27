package iwf.core.command;

import org.immutables.value.Value;

@Value.Immutable
public interface LongRunningActivityDef<O> {

    String getActivityType();

    Class<O> getOutputType();
}
