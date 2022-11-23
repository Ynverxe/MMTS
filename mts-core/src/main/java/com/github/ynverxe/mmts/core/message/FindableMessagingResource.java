package com.github.ynverxe.mmts.core.message;

import com.github.ynverxe.mmts.core.format.FormattingMetricsHolder;
import com.github.ynverxe.mmts.core.format.SimpleFormattingMetricsHolder;
import com.github.ynverxe.mmts.core.resource.FindableResource;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.Supplier;

public class FindableMessagingResource extends FindableResource implements MessagingResource {

    protected Object guarantee;

    protected FindableMessagingResource(String typeName, Class<?> messageClass, String path, boolean abstractValue, Object guarantee) {
        super(typeName, messageClass, path, abstractValue);
        this.guarantee = guarantee;
    }

    protected FindableMessagingResource(String typeName, Class<?> messageClass, String path, boolean abstractValue) {
        this(typeName, messageClass, path, abstractValue, null);
    }

    @Override
    @SuppressWarnings("rawtypes")
    public @NotNull Object guaranteedValue() {
        if (guarantee != null) {
            return guarantee instanceof Supplier ? ((Supplier) guarantee).get() : guarantee;
        }

        return path();
    }

    @Override
    public @Nullable FindableResource model() {
        return this;
    }

    @Override
    public @NotNull MessagingResource or(@NotNull Object guarantee) {
        return new FindableMessagingResource(
                typeName(),
                messageClass(),
                path(),
                abstractValue(),
                (Supplier<?>) () -> {
                    Object oldGuarantee = this.guarantee;
                    if (oldGuarantee instanceof Supplier) {
                        oldGuarantee = ((Supplier<?>) oldGuarantee).get();
                    }

                    return oldGuarantee != null ? oldGuarantee : guarantee;
                }
        );
    }

    @Override
    public @NotNull FormattingMetricsHolder copy() {
        MessagingResource messagingResource = new FindableMessagingResource(
                typeName(),
                messageClass(),
                path(),
                abstractValue(),
                guarantee
        );

        handleChildCopy((SimpleFormattingMetricsHolder) messagingResource);
        return messagingResource;
    }

    @Override
    public @NotNull MessagingResource mutableCopy() {
        return (MessagingResource) FindableMessagingResource.this.copy();
    }
}