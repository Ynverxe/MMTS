package com.github.ynverxe.mmts.core.entity;

import com.github.ynverxe.mmts.core.MMTSHandler;
import org.jetbrains.annotations.NotNull;

public interface EntityStrategy<E> {

    void onNoMessageFound(
            @NotNull MMTSHandler mmtsHandler,
            @NotNull E entity,
            @NotNull String missingPath
    );
}