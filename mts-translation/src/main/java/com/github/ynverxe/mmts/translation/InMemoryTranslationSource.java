package com.github.ynverxe.mmts.translation;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * Store translation data in memory.
 */
public class InMemoryTranslationSource implements TranslationSource {

    private final Map<String, Object> objectMap = new HashMap<>();

    @Override
    public @Nullable Object findData(@Nullable String path) {
        return objectMap.get(path);
    }

    public @NotNull InMemoryTranslationSource addMessageData(@NotNull String key, @Nullable Object value) {
        this.objectMap.put(Objects.requireNonNull(key, "key"), value);
        return this;
    }
}