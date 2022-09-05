package iwf.core.command;

public final class LongRunningActivityCommandResult {

    private final String activityType;
    private final String activityCommandId;

    private final Object output;
    private final ActivityStatus activityStatus;
    private final ActivityTimeoutType activityTimeoutType;

    public LongRunningActivityCommandResult(final String activityType, final String activityCommandId, final Object output, final ActivityStatus activityStatus, final ActivityTimeoutType activityTimeoutType) {
        this.activityType = activityType;
        this.activityCommandId = activityCommandId;
        this.output = output;
        this.activityStatus = activityStatus;
        this.activityTimeoutType = activityTimeoutType;
    }

    public String getActivityType() {
        return activityType;
    }

    public Object getOutput() {
        return output;
    }

    public ActivityTimeoutType getActivityTimeoutType() {
        return activityTimeoutType;
    }

    public ActivityStatus getActivityStatus() {
        return activityStatus;
    }

    public String getActivityCommandId() {
        return activityCommandId;
    }
}