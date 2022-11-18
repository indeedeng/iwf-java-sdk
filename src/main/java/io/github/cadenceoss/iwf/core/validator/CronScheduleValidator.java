package io.github.cadenceoss.iwf.core.validator;

import com.cronutils.model.definition.CronDefinition;
import com.cronutils.model.definition.CronDefinitionBuilder;
import com.cronutils.parser.CronParser;

import java.util.Optional;

import static com.cronutils.model.CronType.UNIX;

public class CronScheduleValidator {
    public static String validate(Optional<String> cronTab) {
        if (cronTab.isEmpty())
            return null;
        CronDefinition cronDefinition =
                CronDefinitionBuilder.instanceDefinitionFor(UNIX);
        CronParser parser = new CronParser(cronDefinition);
        parser.parse(cronTab.get());
        return cronTab.get();
    }
}
