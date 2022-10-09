package com.github.ynverxe.mmts.core.util;

import java.util.Arrays;

public final class ComposedKey {
    private final Object[] objects;

    public ComposedKey(Object... objects) {
        this.objects = objects;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ComposedKey that = (ComposedKey) o;
        return Arrays.equals(objects, that.objects);
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(objects);
    }
}