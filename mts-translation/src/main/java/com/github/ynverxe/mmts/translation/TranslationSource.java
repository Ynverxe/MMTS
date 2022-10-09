package com.github.ynverxe.mmts.translation;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Is the data source of some language translation.
 */
public interface TranslationSource {

    /**
     * @param path - The path to find the data.
     * @return The found object (can be null) or null if there's any result.
     */
    @Nullable Object findData(@NotNull String path);

    /**
     * @return a new {@link InMemoryTranslationSource} source.
     */
    static @NotNull InMemoryTranslationSource newMemorySource() {
        return new InMemoryTranslationSource();
    }
}