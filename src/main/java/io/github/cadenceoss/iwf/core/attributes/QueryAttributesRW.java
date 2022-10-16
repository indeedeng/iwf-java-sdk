package io.github.cadenceoss.iwf.core.attributes;

public interface QueryAttributesRW {
    <T> T get(String key, Class<T> type);
    void set(String key, Object value);
}
