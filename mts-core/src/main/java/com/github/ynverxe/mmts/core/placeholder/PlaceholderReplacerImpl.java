package com.github.ynverxe.mmts.core.placeholder;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.Objects;

public class PlaceholderReplacerImpl implements PlaceholderReplacer {

    private final char startDelimiter, endDelimiter;
    private final Map<String, PlaceholderValueProvider> placeholderApplierMap;

    public PlaceholderReplacerImpl(
            char startDelimiter,
            char endDelimiter,
            Map<String, PlaceholderValueProvider> placeholderApplierMap
    ) {
        this.startDelimiter = startDelimiter;
        this.endDelimiter = endDelimiter;
        this.placeholderApplierMap = Objects.requireNonNull(placeholderApplierMap);
    }

    @Override
    public @NotNull String replace(@Nullable Object entity, @NotNull String text) {
        StringBuilder builder = new StringBuilder();
        StringBuilder identifierBuilder = new StringBuilder();
        StringBuilder parametersBuilder = new StringBuilder();

        char[] chars = text.toCharArray();

        boolean fail = false;
        boolean reset = false;
        boolean identifiedPlaceholder = false;
        PlaceholderValueProvider placeholderValueProvider = null;

        for (int i = 0; i < chars.length; i++) {
            char currentChar = text.charAt(i);

            if (currentChar != startDelimiter) {
                builder.append(currentChar);
                continue;
            }

            while (++i < chars.length) {
                char c = chars[i];

                if (c == ' ') {
                    fail = true;
                    break;
                }

                if (!identifiedPlaceholder) {
                    if (c == '_') {
                        identifiedPlaceholder = true;
                    } else {
                        if (c == endDelimiter) {
                            fail = true;
                            break;
                        }

                        identifierBuilder.append(c);
                    }

                    continue;
                }

                if (placeholderValueProvider == null) {
                    placeholderValueProvider = placeholderApplierMap.get(identifierBuilder.toString());
                }

                if (placeholderValueProvider == null) {
                    fail = true;
                    break;
                }

                if (c != endDelimiter) {
                    parametersBuilder.append(c);
                    fail = i >= (chars.length - 1);
                    continue;
                }

                String replaced = placeholderValueProvider.getPlaceholderValue(entity, text, parametersBuilder.toString());

                if (replaced != null) {
                    builder.append(replaced);
                }

                fail = replaced == null;
                reset = true;
                break;
            }

            if (fail) {
                builder.append(startDelimiter).append(identifierBuilder);

                if (identifiedPlaceholder) {
                    builder.append("_").append(parametersBuilder);
                }

                if (i < chars.length) {
                    builder.append(chars[i]);
                }
            }

            if (reset || fail) {
                reset = false;
                fail = true;
                identifiedPlaceholder = false;
                placeholderValueProvider = null;
                identifierBuilder.setLength(0);
                parametersBuilder.setLength(0);
            }
        }

        return builder.toString();
    }
}