package iwf.core.command;

public class ActivityOptions {

    // TODO: other optional configs: 1. retryOption, 2. tasklist 3, other detailed timeouts(e.g. scheduleToStart, heartbeat)

    private String activityCommandId; // optional, needed when scheduled multiple activities with same type in a state
    private int startToCloseTimeoutSeconds;

    public ActivityOptions(final int startToCloseTimeoutSeconds) {
        this.activityCommandId = "";
        this.startToCloseTimeoutSeconds = startToCloseTimeoutSeconds;
    }

    public ActivityOptions(final String activityId, final int startToCloseTimeoutSeconds) {
        this.activityCommandId = activityId;
        this.startToCloseTimeoutSeconds = startToCloseTimeoutSeconds;
    }

    public int getStartToCloseTimeoutSeconds() {
        return startToCloseTimeoutSeconds;
    }

    public void setStartToCloseTimeoutSeconds(final int startToCloseTimeoutSeconds) {
        this.startToCloseTimeoutSeconds = startToCloseTimeoutSeconds;
    }

    public String getActivityCommandId() {
        return activityCommandId;
    }

    public void setActivityCommandId(final String activityCommandId) {
        this.activityCommandId = activityCommandId;
    }
}
