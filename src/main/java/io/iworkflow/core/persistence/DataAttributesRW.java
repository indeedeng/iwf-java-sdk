package io.iworkflow.core.persistence;

public interface DataAttributesRW {
    <T> T getDataAttribute(String key, Class<T> type);

    void setDataAttribute(String key, Object value);
}
