package com.github.ynverxe.mmts.core.format;

import com.github.ynverxe.mmts.core.exception.NoCreatorFoundException;
import com.github.ynverxe.mmts.core.placeholder.PlaceholderApplier;
import com.github.ynverxe.mmts.core.placeholder.PlaceholderDelimiterPack;
import com.github.ynverxe.mmts.core.placeholder.PlaceholderReplacer;
import com.github.ynverxe.mmts.translation.TranslationData;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface MessageFormatter {

    @NotNull Object formatMessage(
            @NotNull TranslationData translationData,
            @NotNull String alias,
            @Nullable FormattingContext.Configurator contextConfigurator
    ) throws IllegalArgumentException;

    @Nullable Object tryFormatAndReconstruct(
            @NotNull Object object,
            @Nullable FormattingContext.Configurator contextConfigurator
    );

    @NotNull Object formatAbstractMessage(
            @NotNull TranslationData translationData,
            @Nullable FormattingContext.Configurator contextConfigurator
    ) throws IllegalArgumentException;

    <T> @NotNull T formatMessage(
            @NotNull TranslationData translationData,
            @NotNull Class<T> requiredMessageClass,
            @Nullable FormattingContext.Configurator contextConfigurator
    ) throws NoCreatorFoundException;

    @NotNull String formatString(
            @NotNull String str,
            @Nullable FormattingContext.Configurator contextConfigurator
    );

    @Nullable TranslationData toMessageData(@NotNull Object obj);

    <T> void addMessageCreator(@NotNull Class<T> messageClass, @NotNull MessageExpansion<T> messageExpansion);

    <T> void addFormattingVisitor(@NotNull Class<T> messageClass, @NotNull FormattingInterceptor<T> formattingInterceptor);

    @SuppressWarnings("UnusedReturnValue")
    void addPlaceholderApplier(@NotNull String identifier, @NotNull PlaceholderApplier placeholderApplier);

    @NotNull PlaceholderReplacer createPlaceholderReplacer(char startDelimiter, char endDelimiter);

    @NotNull PlaceholderReplacer createPlaceholderReplacer(@NotNull PlaceholderDelimiterPack placeholderDelimiterPack);

    void bindMessageTypeAlias(@NotNull String alias, @NotNull Class<?> messageType);

    static @NotNull MessageFormatter newMessageFormatter() {
        return new MessageFormatterImpl();
    }
}