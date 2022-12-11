package io.github.cadenceoss.iwf.core.persistence;

public interface SearchAttributesRW {

    Long getSearchAttributeInt64(String key);

    void setSearchAttributeInt64(String key, Long value);

    String getSearchAttributeKeyword(String key);

    void setSearchAttributeKeyword(String key, String value);
}
