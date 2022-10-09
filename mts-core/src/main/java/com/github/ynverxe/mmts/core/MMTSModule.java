package com.github.ynverxe.mmts.core;

import com.github.ynverxe.mmts.core.placeholder.PlaceholderApplier;
import com.github.ynverxe.mmts.core.placeholder.PlaceholderReplacer;
import com.github.ynverxe.mmts.core.format.MessageExpansion;
import com.github.ynverxe.mmts.core.format.FormattingInterceptor;
import com.github.ynverxe.mmts.translation.TranslationSource;
import org.jetbrains.annotations.NotNull;

public interface MMTSModule {

    default <E> EntityHandlerContainer<E> getEntityContainer(@NotNull Class<E> entityClass, boolean hierarchyFind) {
        MMTSHandler mmtsHandler = getMMTSHandler();

        if (mmtsHandler == null)
            throw new IllegalStateException("MMTSHandler is not initialized");

        return hierarchyFind ? mmtsHandler.findContainerByHierarchy(entityClass) : mmtsHandler.getContainerFor(entityClass);
    }

    default void addTranslationSource(@NotNull String lang, @NotNull TranslationSource translationSource) {
        MMTSHandler mmtsHandler = getMMTSHandler();

        if (mmtsHandler == null)
            throw new IllegalStateException("MMTSHandler is not initialized");

        mmtsHandler.addTranslationSource(lang, translationSource);
    }

    default <T> void installExpansion(@NotNull Class<T> messageType, @NotNull MessageExpansion<T> messageExpansion) {
        MMTSHandler mmtsHandler = getMMTSHandler();

        if (mmtsHandler == null)
            throw new IllegalStateException("MMTSHandler is not initialized");

        mmtsHandler.addMessageCreator(messageType, messageExpansion);
    }

    default <T> void addFormattingVisitor(@NotNull Class<T> messageType, @NotNull FormattingInterceptor<T> formattingInterceptor) {
        MMTSHandler mmtsHandler = getMMTSHandler();

        if (mmtsHandler == null)
            throw new IllegalStateException("MMTSHandler is not initialized");

        mmtsHandler.addFormattingVisitor(messageType, formattingInterceptor);
    }

    default @NotNull PlaceholderReplacer createPlaceholderReplacer(char startDelimiter, char endDelimiter) {
        MMTSHandler mmtsHandler = getMMTSHandler();

        if (mmtsHandler == null)
            throw new IllegalStateException("MMTSHandler is not initialized");

        return mmtsHandler.createPlaceholderReplacer(startDelimiter, endDelimiter);
    }

    default void installPlaceholderApplier(@NotNull String identifier, @NotNull PlaceholderApplier placeholderApplier) {
        MMTSHandler mmtsHandler = getMMTSHandler();

        if (mmtsHandler == null)
            throw new IllegalStateException("MMTSHandler is not initialized");

        mmtsHandler.addPlaceholderApplier(identifier, placeholderApplier);
    }

    default void bindMessageTypeAlias(@NotNull String alias, @NotNull Class<?> messageType) {
        MMTSHandler mmtsHandler = getMMTSHandler();

        if (mmtsHandler == null)
            throw new IllegalStateException("MMTSHandler is not initialized");

        mmtsHandler.bindMessageTypeAlias(alias, messageType);
    }

    void setMMTSHandler(MMTSHandler mmtsHandler);

    MMTSHandler getMMTSHandler();

    void configure();

}