package iwf.core.command;

import iwf.gen.models.CommandRequest.DeciderTriggerTypeEnum;
import org.immutables.value.Value;

import java.util.List;

@Value.Immutable
public abstract class CommandRequest {
    public abstract List<BaseCommand> getCommands();

    public abstract DeciderTriggerTypeEnum getDeciderTriggerType();

    // empty command request will jump to decide stage immediately.
    // It doesn't matter whatever DeciderTriggerType is provided. But it's required so we have to put one.
    public static final CommandRequest empty = ImmutableCommandRequest.builder().deciderTriggerType(DeciderTriggerTypeEnum.ALL_COMMAND_COMPLETED).build();

    public static CommandRequest forAllCommandCompleted(final Iterable<BaseCommand> commands) {
        return ImmutableCommandRequest.builder().addAllCommands(commands).deciderTriggerType(DeciderTriggerTypeEnum.ALL_COMMAND_COMPLETED).build();
    }

    public static CommandRequest forAnyCommandsCompleted(final Iterable<BaseCommand> commands) {
        return ImmutableCommandRequest.builder().addAllCommands(commands).deciderTriggerType(DeciderTriggerTypeEnum.ANY_COMMAND_COMPLETED).build();
    }

    public static CommandRequest forAnyCommandClosed(final Iterable<BaseCommand> commands) {
        return ImmutableCommandRequest.builder().addAllCommands(commands).deciderTriggerType(DeciderTriggerTypeEnum.ANY_COMMAND_CLOSED).build();
    }
}
