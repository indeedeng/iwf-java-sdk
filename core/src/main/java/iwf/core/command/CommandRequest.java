package iwf.core.command;

import java.util.Arrays;
import java.util.List;

public class CommandRequest {
    private final List<BaseCommand> commands;
    private final DeciderTriggerType deciderTriggerType;

    private CommandRequest(final List<BaseCommand> commands, final DeciderTriggerType deciderTriggerType) {
        this.commands = commands;
        this.deciderTriggerType = deciderTriggerType;
    }

    public static CommandRequest none(){
        return new CommandRequest(null, DeciderTriggerType.ANY_COMMAND_COMPLETED);
    }
    public static CommandRequest forAllCommandCompleted(final BaseCommand... commands) {
        return new CommandRequest(Arrays.asList(commands), DeciderTriggerType.ALL_COMMAND_COMPLETED);
    }

    public static CommandRequest forAnyCommandsCompleted(final BaseCommand... commands) {
        return new CommandRequest(Arrays.asList(commands), DeciderTriggerType.ANY_COMMAND_COMPLETED);
    }

    public static CommandRequest forAnyCommandClosed(final BaseCommand... commands) {
        return new CommandRequest(Arrays.asList(commands), DeciderTriggerType.ANY_COMMAND_CLOSED);
    }
}
