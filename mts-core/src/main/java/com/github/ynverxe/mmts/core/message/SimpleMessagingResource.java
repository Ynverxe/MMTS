package com.github.ynverxe.mmts.core.message;

import com.github.ynverxe.mmts.core.format.SimpleFormattingMetricsHolder;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;
import java.util.function.Supplier;

public class SimpleMessagingResource extends SimpleFormattingMetricsHolder implements MessagingResource {

    private final Object value;

    public SimpleMessagingResource(Object value) {
        this.value = Objects.requireNonNull(value, "value");
    }

    @Override
    @SuppressWarnings("rawtypes")
    public @NotNull Object guaranteedValue() {
        return value instanceof Supplier ? ((Supplier) value).get() : value;
    }

    @Override
    public @NotNull MessagingResource or(@NotNull Object guarantee) {
        return this;
    }
}