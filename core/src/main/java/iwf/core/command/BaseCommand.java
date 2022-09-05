package iwf.core.command;

public class BaseCommand {

    public BaseCommand(final String commandId) {
        this.commandId = commandId;
    }

    private final String commandId;

    public String getCommandId() {
        return commandId;
    }
}





