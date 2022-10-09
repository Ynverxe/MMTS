package com.github.ynverxe.mmts.core.placeholder;

import java.util.Objects;

public final class PlaceholderDelimiterPack {
    private final char startDelimiter, endDelimiter;

    public PlaceholderDelimiterPack(char startDelimiter, char endDelimiter) {
        this.startDelimiter = startDelimiter;
        this.endDelimiter = endDelimiter;
    }

    public char getStartDelimiter() {
        return startDelimiter;
    }

    public char getEndDelimiter() {
        return endDelimiter;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PlaceholderDelimiterPack that = (PlaceholderDelimiterPack) o;
        return startDelimiter == that.startDelimiter && endDelimiter == that.endDelimiter;
    }

    @Override
    public int hashCode() {
        return Objects.hash(startDelimiter, endDelimiter);
    }
}