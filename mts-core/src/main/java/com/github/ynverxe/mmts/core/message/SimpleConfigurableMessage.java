package com.github.ynverxe.mmts.core.message;

import com.github.ynverxe.mmts.core.EntityHandlerContainer;
import com.github.ynverxe.mmts.core.MMTSHandler;
import com.github.ynverxe.mmts.core.remittent.Remittent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class SimpleConfigurableMessage extends AbstractConfigurableMessage {

    private final Object message;

    public SimpleConfigurableMessage(MMTSHandler mmtsHandler, Object message) {
        super(mmtsHandler);
        this.message = message;
    }

    @Override
    protected @Nullable <E> Object findMessage(@NotNull Remittent remittent, EntityHandlerContainer<E> entityHandlerContainer) {
        return message;
    }
}