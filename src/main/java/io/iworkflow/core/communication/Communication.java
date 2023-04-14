package io.iworkflow.core.communication;

import io.iworkflow.core.StateMovement;

public interface Communication {

    /**
     * Publish a value to an internal Channel
     *
     * @param channelName the channel name to send value
     * @param value       the value to be sent
     */
    void publishInternalChannel(String channelName, Object value);

    /**
     * trigger new state movements as the RPC results
     * NOTE: closing workflows like completing/failing are not supported
     * NOTE: Only used in RPC -- cannot be used in state APIs
     *
     * @param stateMovements
     */
    void triggerStateMovements(final StateMovement... stateMovements);
}





