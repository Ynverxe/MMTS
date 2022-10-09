package com.github.ynverxe.mmts.core.message;

import org.jetbrains.annotations.NotNull;

public interface Messenger {

    @NotNull SimpleConfigurableMessage prepareMessage(@NotNull Object message);

    @NotNull TranslatingConfigurableMessage translating(@NotNull String path, @NotNull Class<?> messageClass);

    @NotNull TranslatingConfigurableMessage translatingAbstractMessage(@NotNull String path);

}