package io.github.cadenceoss.iwf.core.validator;

import com.cronutils.model.definition.CronDefinition;
import com.cronutils.model.definition.CronDefinitionBuilder;
import com.cronutils.parser.CronParser;

import static com.cronutils.model.CronType.UNIX;

public class CronScheduleValidator {
    public static String validate(String cronTab) {
        if (cronTab == null)
            return null;
        CronDefinition cronDefinition =
                CronDefinitionBuilder.instanceDefinitionFor(UNIX);
        CronParser parser = new CronParser(cronDefinition);
        parser.parse(cronTab);
        return cronTab;
    }
}
