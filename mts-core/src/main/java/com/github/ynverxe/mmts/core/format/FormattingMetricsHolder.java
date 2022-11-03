package com.github.ynverxe.mmts.core.format;

import com.github.ynverxe.mmts.core.placeholder.PlaceholderDelimiterPack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public interface FormattingMetricsHolder {

    boolean isSkipPlaceholderFormatting();

    @NotNull FormattingMetricsHolder skipPlaceholderFormatting(boolean skipPlaceholderFormatting);

    @NotNull FormattingMetricsHolder replacements(@NotNull Object... replacements);

    @NotNull FormattingMetricsHolder placeholderDelimiter(@NotNull PlaceholderDelimiterPack placeholderDelimiterPack);

    @NotNull FormattingMetricsHolder placeholderDelimiter(char delimiter);

    @NotNull FormattingMetricsHolder data(@NotNull String key, @NotNull Object value);

    @Nullable Object findData(@NotNull String key);

    @Nullable <V> V findData(@NotNull String key, @NotNull Class<V> valueClass);

    @NotNull FormattingMetricsHolder copy();

    @NotNull <T> Optional<T> optionalDataGet(String key, Class<T> clazz);

    static @NotNull FormattingMetricsHolder create() {
        return new SimpleFormattingMetricsHolder();
    }
}