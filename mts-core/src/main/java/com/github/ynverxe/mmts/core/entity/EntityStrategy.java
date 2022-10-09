package com.github.ynverxe.mmts.core.entity;

import com.github.ynverxe.mmts.core.message.Messenger;
import org.jetbrains.annotations.NotNull;

public interface EntityStrategy<E> {

    void onNoMessageFound(
            @NotNull Messenger messenger,
            @NotNull E entity,
            @NotNull String path
    );
}