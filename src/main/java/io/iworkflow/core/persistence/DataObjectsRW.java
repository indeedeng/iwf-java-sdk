package io.iworkflow.core.persistence;

public interface DataObjectsRW {
    <T> T getDataObject(String key, Class<T> type);

    void setDataObject(String key, Object value);
}
