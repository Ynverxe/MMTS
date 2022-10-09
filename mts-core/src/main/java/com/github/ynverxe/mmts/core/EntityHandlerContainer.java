package com.github.ynverxe.mmts.core;

import com.github.ynverxe.mmts.core.entity.EntityResolver;
import com.github.ynverxe.mmts.core.entity.EntityStrategy;
import com.github.ynverxe.mmts.core.entity.MessageSender;
import com.github.ynverxe.mmts.translation.Linguist;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface EntityHandlerContainer<E> {

    @NotNull EntityHandlerContainer<E> bindSender(@NotNull MessageSender<E> messageSender);

    @NotNull EntityHandlerContainer<E> bindStrategy(@NotNull EntityStrategy<E> strategy);

    @NotNull EntityHandlerContainer<E> bindLinguist(@NotNull Linguist<E> linguist);

    @NotNull <T> EntityHandlerContainer<E> bindResolver(@NotNull EntityResolver<E, T> resolver);

    @Nullable MessageSender<E> getSender(@NotNull Class<?> messageClass);

    @Nullable EntityStrategy<E> getStrategy();

    @Nullable Linguist<E> getLinguist();

    @Nullable <T> EntityResolver<E, T> getResolver();

}