package io.iworkflow.core.persistence;

import java.util.List;

public class PersistenceImpl implements Persistence {

    private final DataObjectsRW dataObjectsRW;
    private final SearchAttributesRW searchAttributesRW;
    private final StateLocals stateLocals;

    public PersistenceImpl(final DataObjectsRW dataObjectsRW, final SearchAttributesRW searchAttributesRW, final StateLocals stateLocals) {
        this.dataObjectsRW = dataObjectsRW;
        this.searchAttributesRW = searchAttributesRW;
        this.stateLocals = stateLocals;
    }

    @Override
    public <T> T getDataObject(final String key, final Class<T> type) {
        return dataObjectsRW.getDataObject(key, type);
    }

    @Override
    public void setDataObject(final String key, final Object value) {
        dataObjectsRW.setDataObject(key, value);
    }

    @Override
    public Long getSearchAttributeInt64(final String key) {
        return searchAttributesRW.getSearchAttributeInt64(key);
    }

    @Override
    public void setSearchAttributeInt64(final String key, final Long value) {
        searchAttributesRW.setSearchAttributeInt64(key, value);
    }

    @Override
    public Double getSearchAttributeDouble(final String key) {
        return searchAttributesRW.getSearchAttributeDouble(key);
    }

    @Override
    public void setSearchAttributeDouble(final String key, final Double value) {
        searchAttributesRW.setSearchAttributeDouble(key, value);
    }

    @Override
    public Boolean getSearchAttributeBoolean(final String key) {
        return searchAttributesRW.getSearchAttributeBoolean(key);
    }

    @Override
    public void setSearchAttributeBoolean(final String key, final Boolean value) {
        searchAttributesRW.setSearchAttributeBoolean(key, value);
    }

    @Override
    public String getSearchAttributeKeyword(final String key) {
        return searchAttributesRW.getSearchAttributeKeyword(key);
    }

    @Override
    public void setSearchAttributeKeyword(final String key, final String value) {
        searchAttributesRW.setSearchAttributeKeyword(key, value);
    }

    @Override
    public String getSearchAttributeText(final String key) {
        return searchAttributesRW.getSearchAttributeText(key);
    }

    @Override
    public void setSearchAttributeText(final String key, final String value) {
        searchAttributesRW.setSearchAttributeText(key, value);
    }

    @Override
    public String getSearchAttributeDatetime(final String key) {
        return searchAttributesRW.getSearchAttributeDatetime(key);
    }

    @Override
    public void setSearchAttributeDatetime(final String key, final String value) {
        searchAttributesRW.setSearchAttributeDatetime(key, value);
    }

    @Override
    public List<String> getSearchAttributeKeywordArray(final String key) {
        return searchAttributesRW.getSearchAttributeKeywordArray(key);
    }

    @Override
    public void setSearchAttributeKeywordArray(final String key, final List<String> value) {
        searchAttributesRW.setSearchAttributeKeywordArray(key, value);
    }

    @Override
    public void setStateLocal(final String key, final Object value) {
        stateLocals.setStateLocal(key, value);
    }

    @Override
    public <T> T getStateLocal(final String key, final Class<T> type) {
        return stateLocals.getStateLocal(key, type);
    }

    @Override
    public void recordStateEvent(final String key, final Object... eventData) {
        stateLocals.recordStateEvent(key, eventData);
    }
}
