package iwf.core.attributes;

import java.util.List;

public class AttributeLoadingPolicy {
    private final AttributeLoadingType attributeLoadingType;
    private final List<String> attributeKeys;

    public AttributeLoadingPolicy(final AttributeLoadingType attributeLoadingType, final List<String> attributeKeys) {
        this.attributeLoadingType = attributeLoadingType;
        this.attributeKeys = attributeKeys;
    }

    public static AttributeLoadingPolicy getLoadAllWithoutLocking() {
        return new AttributeLoadingPolicy(AttributeLoadingType.LOAD_ALL_WITHOUT_LOCKING, null);
    }
}
