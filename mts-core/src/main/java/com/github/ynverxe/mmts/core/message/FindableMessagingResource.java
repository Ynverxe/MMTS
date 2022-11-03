package com.github.ynverxe.mmts.core.message;

import com.github.ynverxe.mmts.core.resource.FindableResource;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class FindableMessagingResource extends FindableResource implements MessagingResource {

    protected FindableMessagingResource(String typeName, Class<?> messageClass, String path, boolean abstractValue) {
        super(typeName, messageClass, path, abstractValue);
    }

    @Override
    public @NotNull Object guaranteedValue() {
        return path();
    }

    @Override
    public @Nullable FindableResource model() {
        return this;
    }
}