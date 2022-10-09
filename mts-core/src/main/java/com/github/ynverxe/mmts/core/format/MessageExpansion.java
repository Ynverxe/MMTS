package com.github.ynverxe.mmts.core.format;

import com.github.ynverxe.mmts.translation.TranslationData;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface MessageExpansion<T> {

    @NotNull T createNewMessage(
            @NotNull TranslationData translationData,
            @NotNull MessageFormatter messageFormatter,
            @NotNull FormattingContext formattingContext
    );

    @Nullable TranslationData dismountAsData(@NotNull T obj);

}