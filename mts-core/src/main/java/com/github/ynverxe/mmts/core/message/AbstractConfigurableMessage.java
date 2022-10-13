package com.github.ynverxe.mmts.core.message;

import com.github.ynverxe.mmts.core.EntityHandlerContainer;
import com.github.ynverxe.mmts.core.MMTSHandler;
import com.github.ynverxe.mmts.core.entity.MessageSender;
import com.github.ynverxe.mmts.core.format.FormattingContextNamespaces;
import com.github.ynverxe.mmts.core.format.FormattingContext;
import com.github.ynverxe.mmts.core.placeholder.PlaceholderDelimiterPack;
import com.github.ynverxe.mmts.core.remittent.Remittent;
import com.github.ynverxe.mmts.translation.MessageData;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

@SuppressWarnings("unchecked, rawtypes")
public abstract class AbstractConfigurableMessage implements ConfigurableMessage {

    protected final MMTSHandler mmtsHandler;
    protected FormattingContext.Configurator configurator = (context) -> {};

    AbstractConfigurableMessage(MMTSHandler mmtsHandler) {
        this.mmtsHandler = mmtsHandler;
    }

    @Override
    public @NotNull ConfigurableMessage configureContext(
            @NotNull FormattingContext.Configurator contextConfigurator
    ) {
        this.configurator = this.configurator.and(contextConfigurator);
        return this;
    }

    @Override
    public @NotNull ConfigurableMessage replacing(@NotNull Object @NotNull ... replacements) {
        if (replacements.length % 2 != 0) {
            throw new IllegalArgumentException("replacement size must be divisible by 2");
        }

        return configureContext(context -> {
            for (int i = 0; i < replacements.length; i++) {
                String left = Objects.toString(replacements[i]);
                i++;
                String right = Objects.toString(replacements[i]);

                context.setReplacement(left, right);
            }
        });
    }

    @Override
    public @NotNull ConfigurableMessage placeholderDelimiterPack(@NotNull PlaceholderDelimiterPack placeholderDelimiterPack) {
        Objects.requireNonNull(placeholderDelimiterPack, "placeholderDelimiterPack");

        return configureContext(context -> context.addData(FormattingContextNamespaces.PLACEHOLDER_DELIMITER_NAMESPACE, placeholderDelimiterPack));
    }

    @Override
    public @NotNull ConfigurableMessage placeholderDelimiter(char delimiter) {
        return placeholderDelimiterPack(new PlaceholderDelimiterPack(delimiter, delimiter));
    }

    @Override
    public @NotNull ConfigurableMessage skipPlaceholderFormatting(boolean b) {
        return configureContext(context -> context.addData(FormattingContextNamespaces.SKIP_PLACEHOLDER_APPLICATION, b));
    }

    @Override
    public void send(@NotNull Object entityOrEntities) {
        send(entityOrEntities, "");
    }

    @Override
    public void send(@NotNull Object entityOrEntities, @NotNull String mode) {
        if (entityOrEntities instanceof Iterable) {
            @NotNull String finalMode = mode;
            ((Iterable<?>) entityOrEntities).forEach(entity -> send(entity, finalMode));
        } else {
            Object entity = mmtsHandler.tryResolveEntity(entityOrEntities);

            EntityHandlerContainer entityHandlerContainer = findContainer(entity);

            if (entityHandlerContainer == null) {
                throw new IllegalArgumentException("no container found for: " + entity.getClass());
            }

            Remittent remittent = newRemittent(entity, entityHandlerContainer);

            Object message = findMessage(remittent, entityHandlerContainer);

            if (message == null) return;

            if (mode.isEmpty()) {
                if (message instanceof MessageData) {
                    mode = ((MessageData) message).getString("mode");
                } else {
                    mode = "default";
                }
            }

            message = formatMessage(remittent, entityHandlerContainer, message);

            if (message instanceof Collection) {
                String finalMode = mode;
                ((Iterable<?>) message).forEach(obj -> sendMessage(entityHandlerContainer, entity, finalMode, obj));
            } else {
                sendMessage(entityHandlerContainer, entity, mode, message);
            }
        }
    }

    protected abstract <E> @Nullable Object findMessage(@NotNull Remittent remittent, EntityHandlerContainer<E> entityHandlerContainer);

    protected <E> Object formatMessage(
            @NotNull Remittent remittent,
            @NotNull EntityHandlerContainer<E> entityHandlerContainer,
            @NotNull Object message
    ) {
        FormattingContext.Configurator contextConfigurator = context -> {
            context.addData(FormattingContextNamespaces.REMITTENT_NAMESPACE, remittent);
            configurator.configure(context);
        };

        if (message instanceof Collection) {
            List messageList = new ArrayList();

            ((Collection) message).forEach(individualMessage -> {
                individualMessage = mmtsHandler.formatString((String) individualMessage, contextConfigurator);
                messageList.add(individualMessage);
            });

            message = messageList;
        } else {
            if (message instanceof String) {
                message = mmtsHandler.formatString((String) message, contextConfigurator);
            } else {
                message = mmtsHandler.tryFormatAndReconstruct(message, contextConfigurator);
            }
        }

        return message;
    }

    protected Remittent newRemittent(@NotNull Object entity, @NotNull EntityHandlerContainer entityHandlerContainer) {
        return Remittent.newRemittent(entity, null);
    }

    private void sendMessage(
            EntityHandlerContainer entityHandlerContainer,
            Object entity,
            String mode,
            Object message
    ) {
        MessageSender sender = entityHandlerContainer.getSender(message.getClass());

        if (sender == null) {
            String errorMessage = "No sender found for (entity=" + entity.getClass() + ", messageClass=" + message.getClass() + ")";
            throw new RuntimeException(errorMessage);
        }

        sender.sendMessage(entity, mode, message);
    }

    private @Nullable EntityHandlerContainer findContainer(@NotNull Object entity) {
        Class entityClass = entity.getClass();

        return mmtsHandler.findContainerByHierarchy(entityClass);
    }
}