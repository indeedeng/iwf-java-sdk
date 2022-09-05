package iwf.core.attributes;

public final class SearchAttributeDef<T> {
    private String key;
    private Class<T> type;

    public SearchAttributeDef(final String key, final Class<T> type) {
        this.key = key;
        this.type = type;
    }

    public Class<T> getSearchAttributeType() {
        return type;
    }

    public String getSearchAttributeKey() {
        return key;
    }
}
