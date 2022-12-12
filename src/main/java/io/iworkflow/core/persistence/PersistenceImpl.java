package io.iworkflow.core.persistence;

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
    public String getSearchAttributeKeyword(final String key) {
        return searchAttributesRW.getSearchAttributeKeyword(key);
    }

    @Override
    public void setSearchAttributeKeyword(final String key, final String value) {
        searchAttributesRW.setSearchAttributeKeyword(key, value);
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
    public void recordStateEvent(final String key, final Object eventData) {
        stateLocals.recordStateEvent(key, eventData);
    }
}
