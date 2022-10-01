package iwf.core.command;

import org.immutables.value.Value;

@Value.Immutable
public abstract class ActivityCommand implements BaseCommand {

    public abstract String getActivityType();

    public abstract ActivityOptions getActivityOptions();

    public abstract Object[] getInput();
}