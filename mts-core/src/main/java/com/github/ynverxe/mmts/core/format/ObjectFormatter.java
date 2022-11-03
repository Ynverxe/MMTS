package com.github.ynverxe.mmts.core.format;

import com.github.ynverxe.mmts.core.exception.NoCreatorFoundException;
import com.github.ynverxe.mmts.core.impl.ObjectFormatterImpl;
import com.github.ynverxe.mmts.core.placeholder.PlaceholderValueProvider;
import com.github.ynverxe.mmts.core.placeholder.PlaceholderDelimiterPack;
import com.github.ynverxe.mmts.core.placeholder.PlaceholderReplacer;
import com.github.ynverxe.mmts.translation.ResourceData;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface ObjectFormatter {

    @NotNull Object formatData(
            @NotNull ResourceData resourceData,
            @NotNull String alias,
            @Nullable FormattingMetricsHolder formattingMetricsHolder
    ) throws IllegalArgumentException;

    @Nullable Object tryFormatAndReconstruct(
            @NotNull Object object,
            @Nullable FormattingMetricsHolder formattingMetricsHolder
    );

    @NotNull Object formatAbstractResource(
            @NotNull ResourceData resourceData,
            @Nullable FormattingMetricsHolder formattingMetricsHolder
    ) throws IllegalArgumentException;

    <T> @NotNull T formatData(
            @NotNull ResourceData resourceData,
            @NotNull Class<T> requiredMessageClass,
            @Nullable FormattingMetricsHolder formattingMetricsHolder
    ) throws NoCreatorFoundException;

    @NotNull String formatString(
            @NotNull String str,
            @Nullable FormattingMetricsHolder formattingMetricsHolder
    );

    @Nullable ResourceData toResourceData(@NotNull Object obj);

    <T> void addMessageCreator(@NotNull Class<T> messageClass, @NotNull ObjectExpansion<T> objectExpansion);

    <T> void addFormattingVisitor(@NotNull Class<T> messageClass, @NotNull FormattingInterceptor<T> formattingInterceptor);

    void addPlaceholderValueProvider(@NotNull String identifier, @NotNull PlaceholderValueProvider placeholderValueProvider);

    @NotNull PlaceholderReplacer createPlaceholderReplacer(char startDelimiter, char endDelimiter);

    @NotNull PlaceholderReplacer createPlaceholderReplacer(@NotNull PlaceholderDelimiterPack placeholderDelimiterPack);

    void bindMessageTypeAlias(@NotNull String alias, @NotNull Class<?> messageType);

    static @NotNull ObjectFormatter newObjectFormatter() {
        return new ObjectFormatterImpl();
    }
}