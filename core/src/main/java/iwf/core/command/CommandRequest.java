package iwf.core.command;

import org.immutables.value.Value;

import java.util.List;

@Value.Immutable
public interface CommandRequest {
    List<BaseCommand> getCommands();

    DeciderTriggerType getDeciderTriggerType();

    // empty command request will jump to decide stage immediately.
    // It doesn't matter whatever DeciderTriggerType is provided. But it's required so we have to put one.
    CommandRequest empty = ImmutableCommandRequest.builder().deciderTriggerType(DeciderTriggerType.ALL_COMMAND_COMPLETED).build();

    class helper {
        public static CommandRequest forAllCommandCompleted(final Iterable<BaseCommand> commands) {
            return ImmutableCommandRequest.builder().addAllCommands(commands).deciderTriggerType(DeciderTriggerType.ALL_COMMAND_COMPLETED).build();
        }

        public static CommandRequest forAnyCommandsCompleted(final Iterable<BaseCommand> commands) {
            return ImmutableCommandRequest.builder().addAllCommands(commands).deciderTriggerType(DeciderTriggerType.ANY_COMMAND_COMPLETED).build();
        }

        public static CommandRequest forAnyCommandClosed(final Iterable<BaseCommand> commands) {
            return ImmutableCommandRequest.builder().addAllCommands(commands).deciderTriggerType(DeciderTriggerType.ANY_COMMAND_CLOSED).build();
        }
    }
}
