package io.github.cadenceoss.iwf.core.attributes;

public interface SearchAttributesRW {

    Long getInt64(String key);

    void upsertInt64(String key, Long value);

    String getTextOrKeyword(String key);

    void upsertTextOrKeyword(String key, String value);
}
