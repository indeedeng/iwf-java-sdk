package io.github.cadenceoss.iwf.core.attributes;

public interface SearchAttributesRW {

    Long getInt64(String key);

    void setInt64(String key, Long value);

    String getKeyword(String key);

    void setKeyword(String key, String value);
}
