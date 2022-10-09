package com.github.ynverxe.mmts.core.format;

import org.jetbrains.annotations.NotNull;

public interface FormattingInterceptor<T> {

    @NotNull T visit(@NotNull T message, @NotNull FormattingContext formattingContext);

}