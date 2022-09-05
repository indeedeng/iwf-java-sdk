package iwf.core.attributes;

public final class QueryAttributeDef<T> {
    private String key;
    private Class<T> type;

    public QueryAttributeDef(final String key, final Class<T> type) {
        this.key = key;
        this.type = type;
    }

    public Class<T> getQueryAttributeType() {
        return type;
    }

    public String QueryAttributeKey() {
        return key;
    }
}
