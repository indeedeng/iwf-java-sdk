package io.github.cadenceoss.iwf.core.attributes;

import io.github.cadenceoss.iwf.gen.models.KeyValue;

import java.util.List;

public interface QueryAttributesRW {
    <T> T get(String key, Class<T> type);
    void set(String key, Object value);
    List<KeyValue> getUpsertQueryAttributes();
}
