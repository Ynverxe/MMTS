package com.github.ynverxe.mmts.core;

import com.github.ynverxe.mmts.core.format.FormattingContext;
import com.github.ynverxe.mmts.core.format.MessageFormatter;
import com.github.ynverxe.mmts.core.message.Messenger;
import com.github.ynverxe.mmts.translation.SourceCreator;
import com.github.ynverxe.mmts.translation.TranslationFinder;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public interface MMTSHandler extends TranslationFinder, Messenger, MessageFormatter {

    @NotNull Optional<?> translateAndFormat(
            @NotNull Object langOrEntity,
            @NotNull String path,
            @NotNull Class<?> messageClass,
            @Nullable FormattingContext.Configurator contextConfigurator
    );

    @NotNull Optional<?> translateAndFormat(
            @NotNull Object langOrEntity,
            @NotNull String path,
            @Nullable FormattingContext.Configurator contextConfigurator
    );

    @NotNull <E> EntityHandlerContainer<E> getContainerFor(@NotNull Class<E> entityClass);

    @Nullable <E> EntityHandlerContainer<E> findContainerByHierarchy(@NotNull Class<?> entityClass);

    @NotNull Object tryResolveEntity(@NotNull Object entity);

    default void installModule(@NotNull MMTSModule mmtsModule) {
        mmtsModule.setMMTSHandler(this);
        mmtsModule.configure();
        mmtsModule.setMMTSHandler(null);
    }

    static @NotNull MMTSHandler create(@Nullable SourceCreator sourceCreator) {
        return new MMTSHandlerImpl(sourceCreator);
    }
}