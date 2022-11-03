package com.github.ynverxe.mmts.core.message;

import com.github.ynverxe.mmts.core.format.FormattingMetricsHolder;
import com.github.ynverxe.mmts.core.resource.FindableResource;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;
import java.util.function.Supplier;

@SuppressWarnings("unused")
public interface MessagingResource extends FormattingMetricsHolder {

    @NotNull Object guaranteedValue();

    @NotNull MessagingResource or(@NotNull Object guarantee);

    default @Nullable FindableResource model() {
        return null;
    }

    static @NotNull MessagingResource simple(@NotNull Object value) {
        return new SimpleMessagingResource(value);
    }

    default @NotNull MessagingResource or(@NotNull Supplier<Object> guarantee) {
        return new SimpleMessagingResource(guarantee);
    }

    static @NotNull FindableMessagingResource pathOfAbstract(@NotNull String path) {
        Objects.requireNonNull(path, "path");

        return new FindableMessagingResource(null, null, path, true);
    }

    static @NotNull FindableMessagingResource pathWithType(@NotNull String path, @NotNull Class<?> valueType) {
        Objects.requireNonNull(path, "path");
        Objects.requireNonNull(valueType, "valueType");

        return new FindableMessagingResource(null, valueType, path, false);
    }

    static @NotNull FindableMessagingResource pathWithTypeName(@NotNull String path, @NotNull String valueTypeName) {
        Objects.requireNonNull(path, "path");
        Objects.requireNonNull(valueTypeName, "valueTypeName");

        return new FindableMessagingResource(valueTypeName, null, path, false);
    }

    static @NotNull FindableMessagingResource pathOfText(@NotNull String path) {
        return pathWithType(path, String.class);
    }
}