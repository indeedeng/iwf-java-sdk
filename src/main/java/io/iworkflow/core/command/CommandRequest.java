package io.iworkflow.core.command;

import io.iworkflow.gen.models.CommandCombination;
import io.iworkflow.gen.models.DeciderTriggerType;
import org.immutables.value.Value;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Value.Immutable
public abstract class CommandRequest {
    public abstract List<BaseCommand> getCommands();

    public abstract List<CommandCombination> getCommandCombinations();

    public abstract DeciderTriggerType getDeciderTriggerType();

    // empty command request will jump to decide stage immediately.
    // It doesn't matter whatever DeciderTriggerType is provided. But it's required so we have to put one.
    public static final CommandRequest empty = ImmutableCommandRequest.builder().deciderTriggerType(DeciderTriggerType.ALL_COMMAND_COMPLETED).build();

    /**
     * forAllCommandCompleted will wait for all the commands to complete
     *
     * @param commands all the commands
     * @return the command request
     */
    public static CommandRequest forAllCommandCompleted(final BaseCommand... commands) {
        return ImmutableCommandRequest.builder().addAllCommands(Arrays.asList(commands)).deciderTriggerType(DeciderTriggerType.ALL_COMMAND_COMPLETED).build();
    }

    /**
     * forAnyCommandCompleted will wait for any the commands to complete
     *
     * @param commands all the commands
     * @return the command request
     */
    public static CommandRequest forAnyCommandCompleted(final BaseCommand... commands) {
        return ImmutableCommandRequest.builder().addAllCommands(Arrays.asList(commands)).deciderTriggerType(DeciderTriggerType.ANY_COMMAND_COMPLETED).build();
    }

    /**
     * This will wait for any combination to complete.
     * Using this requires every command has a commandId when created.
     * Functionally this one can cover both forAllCommandCompleted, forAnyCommandCompleted. So the other two are like "shortcuts" of it.
     *
     * @param commandCombinationLists a list of different combinations, each combination is a list of String as CommandIds
     * @param commands                all the commands
     * @return the command request
     */
    public static CommandRequest forAnyCommandCombinationCompleted(final List<List<String>> commandCombinationLists, final BaseCommand... commands) {
        final List<CommandCombination> combinations = new ArrayList<>();
        commandCombinationLists.forEach(commandIds -> combinations.add(new CommandCombination().commandIds(commandIds)));
        return ImmutableCommandRequest.builder()
                .commandCombinations(combinations)
                .addAllCommands(Arrays.asList(commands))
                .deciderTriggerType(DeciderTriggerType.ANY_COMMAND_COMBINATION_COMPLETED)
                .build();
    }
}
