package org.mnemosynehermesteam.server.mhrl;

import java.util.List;

public class MHRLRequestBody {
    private final String op;
    private final String key;
    private final Object value;
    private final List<Object> item;
    private final List<String> mapKeys;

    public MHRLRequestBody(String op, String key, Object value, List<Object> item, List<String> mapKeys) {
        this.op = op;
        this.key = key;
        this.value = value;
        this.item = item;
        this.mapKeys = mapKeys;
    }

    public String op() {
        return op;
    }

    public String key() {
        return key;
    }

    public Object value() {
        return value;
    }

    public List<Object> item() {
        return item;
    }

    public List<String> mapKeys() {
        return mapKeys;
    }
}
