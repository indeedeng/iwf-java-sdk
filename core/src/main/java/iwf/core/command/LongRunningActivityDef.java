package iwf.core.command;

public final class LongRunningActivityDef<O> {
    private String activityType;
    private Class<O> outputType;

    public LongRunningActivityDef(final String activityType, final Class<O> outputType) {
        this.activityType = activityType;
        this.outputType = outputType;
    }

    public String getActivityType() {
        return activityType;
    }

    public Class<O> getOutputType() {
        return outputType;
    }
}
