package io.github.cadenceoss.iwf.core.persistence;

// see https://docs.temporal.io/concepts/what-is-a-search-attribute/
// TODO add TEXT, bool, datetime and double
public enum SearchAttributeType {
    KEYWORD,
    INT_64
}
