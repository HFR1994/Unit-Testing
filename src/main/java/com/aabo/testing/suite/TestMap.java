package com.aabo.testing.suite;

import java.util.LinkedHashMap;

public class TestMap<S, O> extends LinkedHashMap {

    public TestMap<S, O> put(String key, Object value) {
        super.put(key, value);
        return this;
    }
}
