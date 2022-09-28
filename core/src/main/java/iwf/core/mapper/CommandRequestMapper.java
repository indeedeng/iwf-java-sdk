package iwf.core.mapper;

import iwf.gen.models.CommandRequest;

public class CommandRequestMapper {
    public static CommandRequest toGenerated(iwf.core.command.CommandRequest commandRequest) {
        return new CommandRequest().deciderTriggerType(commandRequest.getDeciderTriggerType());
    }
}
