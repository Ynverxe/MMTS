package com.github.ynverxe.mmts.core.format;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

@SuppressWarnings("UnusedReturnValue, unused")
public final class FormattingContext {

    private final Class<?> messageClass;
    private final Map<String, Object> extraData;
    private final Map<String, String> replacements;

    private FormattingContext(
            Class<?> messageClass,
            Map<String, Object> extraData,
            Map<String, String> replacements
    ) {
        this.messageClass = messageClass;
        this.extraData = extraData;
        this.replacements = replacements;
    }

    public FormattingContext(Class<?> messageClass) {
        this.messageClass = Objects.requireNonNull(messageClass, "messageClass");
        this.extraData = new HashMap<>();

        this.replacements = new HashMap<>();
    }

    public @Nullable Object getData(@NotNull String key) {
        return getData(key, Object.class);
    }

    public <T> @Nullable T getData(@NotNull String key, @NotNull Class<T> expectedReturnValueType) {
        return expectedReturnValueType.cast(extraData.get(key));
    }

    public <T> @NotNull Optional<T> optionalDataGet(@NotNull String key, @NotNull Class<T> expectedReturnValueType) {
        Object found = getData(key);

        if (expectedReturnValueType.isInstance(found))
            return Optional.of(expectedReturnValueType.cast(found));

        return Optional.empty();
    }

    public @NotNull FormattingContext addData(@NotNull String key, @NotNull Object value) {
        extraData.put(Objects.requireNonNull(key, "key"), value);
        return this;
    }

    public @NotNull Class<?> getMessageClass() {
        return messageClass;
    }

    public @NotNull FormattingContext setReplacement(@NotNull String key, @Nullable String replacement) {
        replacements.put(Objects.requireNonNull(key, "key"), replacement);
        return this;
    }

    public @NotNull Map<String, @Nullable String> getReplacements() {
        return Collections.unmodifiableMap(replacements);
    }

    public @NotNull FormattingContext consume(
            @NotNull FormattingContext other
    ) {
        extraData.putAll(other.extraData);
        replacements.putAll(other.replacements);
        return this;
    }

    public @NotNull FormattingContext copy(@NotNull Class<?> messageClass) {
        return new FormattingContext(
                messageClass,
                new HashMap<>(extraData),
                new HashMap<>(replacements)
        );
    }

    public @NotNull Configurator toConfigurator() {
        return consuming(this);
    }

    public @NotNull FormattingContext copy() {
        return copy(messageClass);
    }

    public interface Configurator {
        void configure(@NotNull FormattingContext context);

        default @NotNull FormattingContext.Configurator and(Configurator configurator) {
            return context -> {
                configure(context);
                configurator.configure(context);
            };
        }
    }

    public static @NotNull Configurator consuming(@NotNull FormattingContext target) {
        Objects.requireNonNull(target, "target is null");

        return context -> context.consume(target);
    }
}