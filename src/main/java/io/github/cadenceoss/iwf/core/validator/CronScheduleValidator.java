package io.github.cadenceoss.iwf.core.validator;

import com.cronutils.model.Cron;
import com.cronutils.model.definition.CronDefinition;
import com.cronutils.model.definition.CronDefinitionBuilder;
import com.cronutils.parser.CronParser;

import static com.cronutils.model.CronType.UNIX;

public class CronScheduleValidator {
    public static boolean isValidCronSchedule(String cronTab) {
        CronDefinition cronDefinition =
                CronDefinitionBuilder.instanceDefinitionFor(UNIX);
        CronParser parser = new CronParser(cronDefinition);
        boolean result = true;
        try {
            parser.parse(cronTab);
        } catch (IllegalArgumentException ex) {
            result = false;
        }
        return result;
    }

    public static void main(String[] args) {
        System.out.println(CronScheduleValidator.isValidCronSchedule("5 0 * 8 *"));
    }
}
