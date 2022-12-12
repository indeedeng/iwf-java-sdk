package io.iworkflow.core.persistence;

import io.iworkflow.core.WorkflowDefinitionException;
import io.iworkflow.gen.models.SearchAttribute;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SearchAttributeRWImpl implements SearchAttributesRW {

    private final Map<String, SearchAttributeType> keyToTypeMap;
    private final Map<String, Long> int64AttributeMap;
    private final Map<String, Long> upsertToServerInt64AttributeMap;
    private final Map<String, String> keywordAttributeMap;
    private final Map<String, String> upsertToServerKeywordAttributeMap;

    public SearchAttributeRWImpl(final Map<String, SearchAttributeType> keyToTypeMap,
                                 final List<SearchAttribute> searchAttributeMap
    ) {
        this.keyToTypeMap = keyToTypeMap;
        int64AttributeMap = new HashMap<>();
        upsertToServerInt64AttributeMap = new HashMap<>();
        keywordAttributeMap = new HashMap<>();
        upsertToServerKeywordAttributeMap = new HashMap<>();

        if (searchAttributeMap != null) {
            searchAttributeMap.forEach((sa) -> {
                final SearchAttributeType type = keyToTypeMap.get(sa.getKey());
                if (type == SearchAttributeType.KEYWORD) {
                    keywordAttributeMap.put(sa.getKey(), sa.getStringValue());
                }
                if (type == SearchAttributeType.INT_64) {
                    int64AttributeMap.put(sa.getKey(), sa.getIntegerValue());
                }
            });
        }
    }

    @Override
    public Long getSearchAttributeInt64(final String key) {
        return int64AttributeMap.get(key);
    }

    @Override
    public void setSearchAttributeInt64(final String key, final Long value) {
        if (!keyToTypeMap.containsKey(key) || keyToTypeMap.get(key) != SearchAttributeType.INT_64) {
            throw new WorkflowDefinitionException(String.format(
                    "key %s is not defined as int64", key));
        }
        int64AttributeMap.put(key, value);
        upsertToServerInt64AttributeMap.put(key, value);
    }

    @Override
    public String getSearchAttributeKeyword(final String key) {
        return keywordAttributeMap.get(key);
    }

    @Override
    public void setSearchAttributeKeyword(final String key, final String value) {
        if (!keyToTypeMap.containsKey(key) || keyToTypeMap.get(key) != SearchAttributeType.KEYWORD) {
            throw new WorkflowDefinitionException(String.format(
                    "key %s is not defined as keyword", key));
        }
        keywordAttributeMap.put(key, value);
        upsertToServerKeywordAttributeMap.put(key, value);
    }

    public Map<String, Long> getUpsertToServerInt64AttributeMap() {
        return upsertToServerInt64AttributeMap;
    }

    public Map<String, String> getUpsertToServerKeywordAttributeMap() {
        return upsertToServerKeywordAttributeMap;
    }
}
