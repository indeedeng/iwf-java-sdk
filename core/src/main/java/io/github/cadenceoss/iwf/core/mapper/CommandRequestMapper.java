package io.github.cadenceoss.iwf.core.mapper;

import io.github.cadenceoss.iwf.core.command.CommandRequest;

public class CommandRequestMapper {
    public static io.github.cadenceoss.iwf.gen.models.CommandRequest toGenerated(CommandRequest commandRequest) {
        return new io.github.cadenceoss.iwf.gen.models.CommandRequest().deciderTriggerType(commandRequest.getDeciderTriggerType());
    }
}
