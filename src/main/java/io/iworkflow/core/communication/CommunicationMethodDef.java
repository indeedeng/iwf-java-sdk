package io.iworkflow.core.communication;

public interface CommunicationMethodDef {
    String getName();
    Class getType();
    Boolean isPrefix();
}
