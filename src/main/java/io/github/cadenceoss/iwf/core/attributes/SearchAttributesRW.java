package io.github.cadenceoss.iwf.core.attributes;

public interface SearchAttributesRW {

    Long getInt64(String key);

    void upsertInt64(String key, Long value);

    String getKeyword(String key);

    void upsertKeyword(String key, String value);
}
