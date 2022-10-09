package com.github.ynverxe.mmts.core.entity;

import org.jetbrains.annotations.NotNull;

public interface MessageSender<E> {

    void sendMessage(@NotNull E entity, @NotNull String mode, @NotNull Object message);

    boolean offer(@NotNull Class<?> messageClass);

}