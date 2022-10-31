package io.github.cadenceoss.iwf.core.command;

public interface InterStateChannel {

    void publish(String channelName, Object value);
}





