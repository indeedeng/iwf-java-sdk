package io.iworkflow.core.communication;

import io.iworkflow.core.StateMovement;

public interface Communication {

    /**
     * Get the size of the internal channel(including the one being sent in the buffer)
     * NOTE: currently only supported in RPC
     * @param channelName the channel name to get size
     * @return the size of the internal channel
     */
    int getInternalChannelSize(final String channelName);

    /**
     * Get the size of the signal channel(including the one being sent in the buffer)
     * NOTE: currently only supported in RPC
     * @param channelName the channel name to get size
     * @return the size of the signal channel
     */
    int getSignalChannelSize(final String channelName);

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
     * @param stateMovements the state movements to trigger
     */
    void triggerStateMovements(final StateMovement... stateMovements);
}





