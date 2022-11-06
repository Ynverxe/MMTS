package com.github.ynverxe.mmts.core.message;

import com.github.ynverxe.mmts.core.format.FormattingMetricsHolder;
import com.github.ynverxe.mmts.core.placeholder.PlaceholderDelimiterPack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public class ImmutableDelegatingMessagingResource implements MessagingResource {

    private final MessagingResource delegate;

    public ImmutableDelegatingMessagingResource(MessagingResource delegate) {
        this.delegate = delegate.mutableCopy();
    }

    @Override
    public boolean isSkipPlaceholderFormatting() {
        return delegate.isSkipPlaceholderFormatting();
    }

    @Override
    public @NotNull FormattingMetricsHolder skipPlaceholderFormatting(boolean skipPlaceholderFormatting) {
        throw new UnsupportedOperationException("immutable resource");
    }

    @Override
    public @NotNull FormattingMetricsHolder replacements(@NotNull Object... replacements) {
        throw new UnsupportedOperationException("immutable resource");
    }

    @Override
    public @NotNull FormattingMetricsHolder placeholderDelimiter(@NotNull PlaceholderDelimiterPack placeholderDelimiterPack) {
        throw new UnsupportedOperationException("immutable resource");
    }

    @Override
    public @NotNull FormattingMetricsHolder placeholderDelimiter(char delimiter) {
        throw new UnsupportedOperationException("immutable resource");
    }

    @Override
    public @NotNull FormattingMetricsHolder data(@NotNull String key, @NotNull Object value) {
        throw new UnsupportedOperationException("immutable resource");
    }

    @Override
    public @Nullable Object findData(@NotNull String key) {
        return delegate.findData(key);
    }

    @Override
    public <V> @Nullable V findData(@NotNull String key, @NotNull Class<V> valueClass) {
        return delegate.findData(key, valueClass);
    }

    @Override
    public @NotNull FormattingMetricsHolder copy() {
        return delegate.copy();
    }

    @Override
    public @NotNull <T> Optional<T> optionalDataGet(String key, Class<T> clazz) {
        return delegate.optionalDataGet(key, clazz);
    }

    @Override
    public @NotNull Object guaranteedValue() {
        return delegate.guaranteedValue();
    }

    @Override
    public @NotNull MessagingResource or(@NotNull Object guarantee) {
        return delegate.or(guarantee);
    }

    @Override
    public @NotNull MessagingResource mutableCopy() {
        return delegate.mutableCopy();
    }
}