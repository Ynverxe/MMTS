package com.github.ynverxe.mmts.core.entity;

import org.jetbrains.annotations.NotNull;

public interface EntityResolver<F, T> {

    @NotNull T resolveEntity(@NotNull F entity);

}