package iwf.core.command;

import org.immutables.value.Value;

@Value.Immutable
public interface ActivityCommand extends BaseCommand {

    String getActivityType();

    ActivityOptions getActivityOptions();

    Object[] getInput();
}