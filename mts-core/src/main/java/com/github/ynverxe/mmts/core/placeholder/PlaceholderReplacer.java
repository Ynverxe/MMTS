package com.github.ynverxe.mmts.core.placeholder;

import org.jetbrains.annotations.NotNull;

public interface PlaceholderReplacer {

    @NotNull String replace(@NotNull Object entity, @NotNull String text);

}