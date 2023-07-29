package io.iworkflow.core.command;

import io.iworkflow.core.communication.InternalChannelCommand;
import io.iworkflow.core.communication.SignalCommand;
import org.immutables.value.Value;

import java.util.ArrayList;
import java.util.List;

@Value.Immutable
public abstract class SuperCommand implements BaseCommand {

    public enum Type {
        SIGNAL_CHANNEL,
        INTERNAL_CHANNEL,
    }

    public abstract String getName();
    public abstract int getCount();
    public abstract Type getType();

    public static List<BaseCommand> toList(final SuperCommand superCommand) {
        final List<BaseCommand> commands = new ArrayList<>();

        final boolean hasCommandId = superCommand.getCommandId().isPresent();
        for (int i = 0; i < superCommand.getCount(); i ++) {
            final BaseCommand command;
            if (superCommand.getType() == Type.INTERNAL_CHANNEL) {
                if (hasCommandId) {
                    command = InternalChannelCommand.create(superCommand.getCommandId().get(), superCommand.getName());
                } else {
                    command = InternalChannelCommand.create(superCommand.getName());
                }
            } else {
                if (hasCommandId) {
                    command = SignalCommand.create(superCommand.getCommandId().get(), superCommand.getName());
                } else {
                    command = SignalCommand.create(superCommand.getName());
                }
            }

            commands.add(command);
        }

        return commands;
    }
}
