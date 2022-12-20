package io.iworkflow.core.communication;

public interface Communication {

    /**
     * Publish a value to an interstate Channel
     *
     * @param channelName the channel name to send value
     * @param value       the value to be sent
     */
    void publishInterstateChannel(String channelName, Object value);
}





