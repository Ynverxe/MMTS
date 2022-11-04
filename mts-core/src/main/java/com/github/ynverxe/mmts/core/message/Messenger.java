package com.github.ynverxe.mmts.core.message;

import com.github.ynverxe.mmts.core.format.FormattingMetricsHolder;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface Messenger {

    void dispatchMessage(
            @NotNull Object message,
            @Nullable String mode,
            @NotNull Object entityOrEntities,
            Object... replacements
    );

    void dispatchMessage(
            @NotNull Object message,
            @Nullable String mode,
            @NotNull Object entityOrEntities,
            @NotNull FormattingMetricsHolder formattingMetricsHolder,
            Object... replacements
    );
}