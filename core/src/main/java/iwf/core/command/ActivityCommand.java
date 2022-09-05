package iwf.core.command;

public final class ActivityCommand extends BaseCommand {

    // below are fields supplied by user code via constructor
    private String activityType;
    private Object[] input;
    private ActivityOptions activityOptions;

    public ActivityCommand(final String activityType, final ActivityOptions options, final Object... input) {
        super(options.getActivityCommandId());
        this.activityType = activityType;
        this.input = input;
        this.activityOptions = options;
    }

    public String getActivityType() {
        return activityType;
    }

    public ActivityOptions getActivityOptions() {
        return activityOptions;
    }

    public Object[] getInput() {
        return input;
    }
}