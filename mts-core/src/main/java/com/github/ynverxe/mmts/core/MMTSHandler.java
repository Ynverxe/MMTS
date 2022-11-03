package com.github.ynverxe.mmts.core;

import com.github.ynverxe.mmts.core.format.ObjectFormatter;
import com.github.ynverxe.mmts.core.impl.MMTSHandlerImpl;
import com.github.ynverxe.mmts.core.message.Messenger;
import com.github.ynverxe.mmts.core.resource.FindableResource;
import com.github.ynverxe.mmts.translation.SourceCreator;
import com.github.ynverxe.mmts.translation.TranslationFinder;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

public interface MMTSHandler extends TranslationFinder, Messenger, ObjectFormatter {

    @NotNull Map<Object, Object> processModel(
            @NotNull FindableResource model,
            @NotNull Object... entitiesOrLanguages
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