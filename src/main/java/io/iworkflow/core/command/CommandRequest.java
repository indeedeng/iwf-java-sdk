package io.iworkflow.core.command;

import io.iworkflow.core.exceptions.CommandNotFoundException;
import io.iworkflow.gen.models.CommandCombination;
import io.iworkflow.gen.models.CommandWaitingType;
import org.immutables.value.Value;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Value.Immutable
public abstract class CommandRequest {
    public abstract List<BaseCommand> getCommands();

    public abstract List<CommandCombination> getCommandCombinations();

    public abstract CommandWaitingType getCommandWaitingType();

    // empty command request will jump to decide stage immediately.
    // It doesn't matter whatever CommandWaitingType is provided. But it's required so we have to put one.
    public static final CommandRequest empty = ImmutableCommandRequest.builder().commandWaitingType(CommandWaitingType.ALL_COMPLETED).build();

    /**
     * forAllCommandCompleted will wait for all the commands to complete
     *
     * @param commands all the commands
     * @return the command request
     */
    public static CommandRequest forAllCommandCompleted(final BaseCommand... commands) {
        return ImmutableCommandRequest.builder()
                .addAllCommands(Arrays.asList(commands))
                .commandWaitingType(CommandWaitingType.ALL_COMPLETED)
                .build();
    }

    /**
     * forAnyCommandCompleted will wait for any the commands to complete
     *
     * @param commands all the commands
     * @return the command request
     */
    public static CommandRequest forAnyCommandCompleted(final BaseCommand... commands) {
        return ImmutableCommandRequest.builder()
                .addAllCommands(Arrays.asList(commands))
                .commandWaitingType(CommandWaitingType.ANY_COMPLETED)
                .build();
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
        final List<BaseCommand> allSingleCommands = getAllSingleCommands(commands);
        final List<String> allNonEmptyCommandsIds = allSingleCommands.stream()
                .filter(command -> command.getCommandId().isPresent())
                .map(command -> command.getCommandId().get())
                .collect(Collectors.toList());

        final List<CommandCombination> combinations = new ArrayList<>();
        commandCombinationLists.forEach(commandIds -> {
            commandIds.forEach(commandId -> {
                if (!allNonEmptyCommandsIds.contains(commandId)) {
                    throw new CommandNotFoundException(String.format("Found unknown commandId in the combination list: %s", commandId));
                }
            });
            combinations.add(new CommandCombination().commandIds(commandIds));
        });
        return ImmutableCommandRequest.builder()
                .commandCombinations(combinations)
                .addAllCommands(allSingleCommands)
                .commandWaitingType(CommandWaitingType.ANY_COMBINATION_COMPLETED)
                .build();
    }

    private static List<BaseCommand> getAllSingleCommands(final BaseCommand... commands) {

        return new ArrayList<>(Arrays.asList(commands));
    }
}
