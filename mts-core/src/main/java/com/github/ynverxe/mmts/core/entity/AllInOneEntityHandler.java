package com.github.ynverxe.mmts.core.entity;

import com.github.ynverxe.mmts.core.MMTSModule;
import com.github.ynverxe.mmts.translation.Linguist;
import org.jetbrains.annotations.NotNull;

public abstract class AllInOneEntityHandler<E> implements EntityStrategy<E>, MessageSender<E>, Linguist<E> {

    protected abstract @NotNull Class<E> type();

    public final void register(@NotNull MMTSModule mmtsModule) {
        mmtsModule.getEntityContainer(type(), false)
                .bindStrategy(this)
                .bindLinguist(this)
                .bindSender(this);
    }
}