package com.github.ynverxe.mmts.core.placeholder;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface PlaceholderValueProvider {

    @Nullable String getPlaceholderValue(@Nullable Object entity, @NotNull String text, @NotNull String request);

}