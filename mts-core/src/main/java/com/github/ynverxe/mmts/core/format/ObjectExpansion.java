package com.github.ynverxe.mmts.core.format;

import com.github.ynverxe.mmts.translation.ResourceData;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface ObjectExpansion<T> {

    @NotNull T createNewMessage(
            @NotNull ResourceData resourceData,
            @NotNull ObjectFormatter objectFormatter,
            @NotNull FormattingMetricsHolder formattingMetricsHolder
    );

    @Nullable ResourceData dismountAsData(@NotNull T obj);

}