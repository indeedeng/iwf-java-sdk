package io.iworkflow.core.persistence;

public interface StateLocals {
    /**
     * set a local attribute. The scope of the attribute is only within the execution of this state.
     * Usually it's for passing from State Start API to State Decide API
     * User code must make sure using the same type for both get and set
     *
     * @param key   the key of the stateLocal(scope of the state execution)
     * @param value the value
     */
    void setStateLocal(String key, Object value);

    /**
     * Retrieve a local state attribute
     * User code must make sure using the same type for both get and set
     *
     * @param key the key of the stateLocal(scope of the state execution)
     * @param type the value type
     * @param <T> the value type
     * @return the value
     */
    <T> T getStateLocal(String key, Class<T> type);

    /**
     * Record an arbitrary event in State Start/Decide API for debugging/tracking purpose
     *
     * @param key       the key of the event. Within a Start/Decide API, the same key cannot be used for more than once.
     * @param eventData the data of the event.
     */
    void recordStateEvent(String key, Object eventData);
}
