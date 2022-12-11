package io.github.cadenceoss.iwf.core.communication;

public interface Communication {

    /**
     * Publish a value to an interstate Channel
     *
     * @param channelName
     * @param value
     */
    void publishInterstateChannel(String channelName, Object value);
}





