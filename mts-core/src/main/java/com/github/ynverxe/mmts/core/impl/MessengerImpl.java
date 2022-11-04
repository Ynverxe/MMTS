package com.github.ynverxe.mmts.core.impl;

import com.github.ynverxe.mmts.core.EntityHandlerContainer;
import com.github.ynverxe.mmts.core.MMTSHandler;
import com.github.ynverxe.mmts.core.entity.EntityStrategy;
import com.github.ynverxe.mmts.core.format.FormattingMetricsHolder;
import com.github.ynverxe.mmts.core.message.FindableMessagingResource;
import com.github.ynverxe.mmts.core.message.MessagingResource;
import com.github.ynverxe.mmts.core.message.Messenger;
import com.github.ynverxe.mmts.core.remittent.Remittent;
import com.github.ynverxe.mmts.core.resource.FindableResource;
import com.github.ynverxe.mmts.translation.ResourceData;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.function.Supplier;

@SuppressWarnings("unchecked, rawtypes")
public class MessengerImpl implements Messenger {

    private final MMTSHandler mmtsHandler;

    MessengerImpl(MMTSHandler mmtsHandler) {
        this.mmtsHandler = mmtsHandler;
    }

    @Override
    public void dispatchMessage(@NotNull Object message, @Nullable String mode, @NotNull Object entityOrEntities, Object... replacements) {
        dispatchMessage(message, mode, entityOrEntities, FormattingMetricsHolder.create(), replacements);
    }

    @Override
    public void dispatchMessage(@NotNull Object message, @Nullable String mode, @NotNull Object entityOrEntities, @NotNull FormattingMetricsHolder formattingMetricsHolder, Object... replacements) {
        if (entityOrEntities instanceof Collection) {
            ((Collection<?>) entityOrEntities).forEach(entity -> dispatchMessage(message, mode, entity));
        } else {
            if (message instanceof MessagingResource) {
                dispatchWithResource((MessagingResource) message, mode, entityOrEntities, replacements);
            } else {
                formattingMetricsHolder.replacements(replacements);

                MessagingProcessor messagingProcessor = newMessagingProcessor(
                        entityOrEntities,
                        message,
                        null,
                        formattingMetricsHolder,
                        null
                );

                if (messagingProcessor == null) return;

                messagingProcessor.setMode(mode);

                messagingProcessor.dispatchMessage();
            }
        }
    }

    private void dispatchWithResource(@NotNull MessagingResource messagingResource, @Nullable String mode, @NotNull Object entityOrEntities, Object... replacements) {
        if (messagingResource instanceof FindableMessagingResource) {
            translateAndDispatch((FindableMessagingResource) messagingResource, mode, entityOrEntities, messagingResource::guaranteedValue, replacements);
        } else if (messagingResource.model() != null) {
            //noinspection ConstantConditions
            translateAndDispatch(messagingResource.model(), mode, entityOrEntities, messagingResource::guaranteedValue, replacements);
        } else {
            dispatchMessage(messagingResource.guaranteedValue(), mode, entityOrEntities, messagingResource.copy(), replacements);
        }
    }

    private void translateAndDispatch(
            @NotNull FindableResource findableResource,
            @Nullable String mode,
            @NotNull Object entityOrEntities,
            @NotNull Supplier<Object> defaultValueSupplier,
            Object... replacements
    ) {
        if (entityOrEntities instanceof Collection) {
            ((Collection<?>) entityOrEntities).forEach(entity -> translateAndDispatch(findableResource, mode, entity, defaultValueSupplier, replacements));
        } else {
            findableResource.replacements(replacements);

            MessagingProcessor messagingProcessor = newMessagingProcessor(
                    entityOrEntities,
                    null,
                    findableResource.path(),
                    findableResource.copy(),
                    defaultValueSupplier
            );

            if (messagingProcessor == null) return;

            messagingProcessor.setMode(mode);

            if (!findableResource.abstractValue()) {
                Object type = findableResource.messageClass();
                type = type != null ? type : findableResource.messageClass();

                messagingProcessor.setDefinitiveMessageType(type);
            }

            messagingProcessor.dispatchMessage();
        }
    }

    @SuppressWarnings("ConstantConditions")
    private @Nullable MessagingProcessor newMessagingProcessor(
            Object entity,
            Object message,
            String messagePath,
            FormattingMetricsHolder formattingMetricsHolder,
            Supplier<Object> defaultMessageSupplier
    ) {
        MessagingProcessor messagingProcessor = new MessagingProcessor(mmtsHandler);
        entity = mmtsHandler.tryResolveEntity(entity);

        EntityHandlerContainer entityHandlerContainer = mmtsHandler.findContainerByHierarchy(entity.getClass());
        messagingProcessor.setEntityHandlerContainer(entityHandlerContainer);

        messagingProcessor.setFormattingMetricsHolder(formattingMetricsHolder);

        String lang = null;
        if (messagePath != null) { // translation context
            lang = mmtsHandler.resolveLang(entity);
        }

        messagingProcessor.setRemittent(Remittent.newRemittent(entity, lang));

        if (message != null) {
            messagingProcessor.setMessage(message);
        } else {
            ResourceData resourceData = mmtsHandler.getTranslationData(messagePath, lang);

            if (resourceData.getDataMap().isEmpty()) {
                Object defaultMessage = defaultMessageSupplier.get();

                if (defaultMessage == null) {
                    EntityStrategy entityStrategy = entityHandlerContainer.getStrategy();
                    if (entityStrategy == null)
                        throw new IllegalArgumentException("no strategy found for: " + entity.getClass());

                    entityStrategy.onNoMessageFound(mmtsHandler, entity, messagePath);
                    return null;
                } else {
                    if (defaultMessage instanceof ResourceData) {
                        messagingProcessor.setMessageData((ResourceData) defaultMessage);
                    } else {
                        messagingProcessor.setMessage(defaultMessage);
                    }
                }
            } else {
                messagingProcessor.setMessageData(resourceData);
            }
        }

        return messagingProcessor;
    }
}