package com.github.ynverxe.mmts.core.format;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

import static com.github.ynverxe.mmts.core.format.FormattingContextNamespaces.*;

@SuppressWarnings("unchecked, rawtypes, unused, UnusedReturnValue")
public class FormattingMetricsHolder {

    protected boolean skipPlaceholderFormatting;
    protected final Map<String, Object> extraData = new HashMap<>();

    public boolean isSkipPlaceholderFormatting() {
        return skipPlaceholderFormatting;
    }

    public @NotNull FormattingMetricsHolder skipPlaceholderFormatting(boolean skipPlaceholderFormatting) {
        this.skipPlaceholderFormatting = skipPlaceholderFormatting;

        return this;
    }

    public @NotNull FormattingMetricsHolder data(@NotNull String key, @NotNull Object value) {
        extraData.put(Objects.requireNonNull(key, "key"), value);
        return this;
    }

    public @Nullable Object findData(@NotNull String key) {
        return extraData.get(key);
    }

    public @Nullable <V> V findData(@NotNull String key, @NotNull Class<V> valueClass) {
        return valueClass.cast(findData(key));
    }

    public @NotNull FormattingMetricsHolder replacements(@NotNull Object... replacements) {
        if (replacements.length % 2 != 0)
            throw new IllegalArgumentException("replacements must be divisible by 2");

        Map<String, String> replacementValues = (Map<String, String>) extraData.computeIfAbsent(REPLACEMENT_VALUES,
                (k) -> new HashMap<>());

        for (int i = 0; i < replacements.length; i++) {
            String key = (String) replacements[i];
            i++;
            String value = Objects.toString(replacements[i]);

            replacementValues.put(key, value);
        }

        return this;
    }

    public FormattingMetricsHolder copy() {
        FormattingMetricsHolder formattingMetricsHolder = new FormattingMetricsHolder();
        handleChildCopy(formattingMetricsHolder);

        return formattingMetricsHolder;
    }

    protected void handleChildCopy(FormattingMetricsHolder another) {
        another.skipPlaceholderFormatting = skipPlaceholderFormatting;
        another.extraData.putAll(extraData);

        Map replacementValues = (Map) another.extraData.getOrDefault(REPLACEMENT_VALUES, Collections.emptyMap());
        replacementValues.putAll((Map) extraData.getOrDefault(REPLACEMENT_VALUES, Collections.emptyMap()));
    }

    public <T> Optional<T> optionalDataGet(String key, Class<T> clazz) {
        Object found = findData(key);

        if (clazz.isInstance(found)) {
            return Optional.of((T) found);
        }

        return Optional.empty();
    }
}