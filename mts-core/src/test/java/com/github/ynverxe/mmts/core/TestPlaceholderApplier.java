package com.github.ynverxe.mmts.core;

import com.github.ynverxe.mmts.core.placeholder.PlaceholderApplier;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class TestPlaceholderApplier implements PlaceholderApplier {

    @Override
    public @Nullable String applyPlaceholders(@Nullable Object entity, @NotNull String text, @NotNull String request) {
        if (entity instanceof Identity) {
            switch (request) {
                case "name":
                    return ((Identity) entity).getIdentifier();
                case "lang":
                    return ((Identity) entity).getLang();
            }
        }

        return null;
    }
}