package io.iworkflow.core.persistence;

import java.util.List;

public interface SearchAttributesRW {

    Long getSearchAttributeInt64(String key);

    void setSearchAttributeInt64(String key, Long value);

    Double getSearchAttributeDouble(String key);

    void setSearchAttributeDouble(String key, Double value);

    Boolean getSearchAttributeBoolean(String key);

    void setSearchAttributeBoolean(String key, Boolean value);

    String getSearchAttributeKeyword(String key);

    void setSearchAttributeKeyword(String key, String value);

    String getSearchAttributeText(String key);

    void setSearchAttributeText(String key, String value);

    String getSearchAttributeDatetime(String key);

    // NOTE: TODO: only UTC format is allowed for now for upsert
    void setSearchAttributeDatetime(String key, String value);

    List<String> getSearchAttributeKeywordArray(String key);

    void setSearchAttributeKeywordArray(String key, List<String> value);
}
