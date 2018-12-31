package com.aabo.testing.suite;

import java.util.ArrayList;

public class TestList<O> extends ArrayList {

    public TestList<O> put(Object value) {
        //noinspection unchecked
        super.add(value);
        return this;
    }
}
