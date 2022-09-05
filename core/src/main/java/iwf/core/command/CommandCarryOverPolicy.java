package iwf.core.command;

public class CommandCarryOverPolicy {
    private final CommandCarryOverType commandCarryOverType;

    public static CommandCarryOverPolicy none() {
        return new CommandCarryOverPolicy(CommandCarryOverType.NONE);
    }
    
    public CommandCarryOverPolicy(final CommandCarryOverType commandCarryOverType) {
        this.commandCarryOverType = commandCarryOverType;
    }

    public CommandCarryOverType getCommandCarryOverType() {
        return commandCarryOverType;
    }
}
