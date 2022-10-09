package com.github.ynverxe.mmts.core.remittent;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

public final class Remittent {

    private final Object entity;
    private final String lang;

    private Remittent(Object entity, String lang) {
        this.entity = entity;
        this.lang = lang;
    }

    public @NotNull Object getEntity() {
        return entity;
    }

    public @Nullable String getLang() {
        return lang;
    }

    public static Remittent newRemittent(
            @NotNull Object entity,
            @Nullable String lang
    ) {
        return new Remittent(Objects.requireNonNull(entity, "entity"), lang);
    }
}