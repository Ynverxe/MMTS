package com.github.ynverxe.mmts.core.impl;

import com.github.ynverxe.mmts.core.EntityHandlerContainer;
import com.github.ynverxe.mmts.core.MMTSHandler;
import com.github.ynverxe.mmts.core.entity.MessageSender;
import com.github.ynverxe.mmts.core.format.FormattingContextNamespaces;
import com.github.ynverxe.mmts.core.format.FormattingMetricsHolder;
import com.github.ynverxe.mmts.core.remittent.Remittent;
import com.github.ynverxe.mmts.translation.ResourceData;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@SuppressWarnings("rawtypes, unchecked")
final class MessagingProcessor {

    private final MMTSHandler mmtsHandler;
    private String mode;
    private Remittent remittent;
    private EntityHandlerContainer entityHandlerContainer;
    private FormattingMetricsHolder formattingMetricsHolder;
    private Object message;
    private ResourceData resourceData;
    private Object definitiveMessageType;

    MessagingProcessor(MMTSHandler mmtsHandler) {
        this.mmtsHandler = mmtsHandler;
    }

    void dispatchMessage() {
        Object messageToSend;

        if (message != null) {
            messageToSend = formatMessage(mmtsHandler, message, formattingMetricsHolder);
        } else {
            messageToSend = formatMessageData(mmtsHandler, resourceData, definitiveMessageType, formattingMetricsHolder);
        }

        handleMessageDispatch(entityHandlerContainer, messageToSend, mode, remittent.getEntity());
    }

    void setRemittent(Remittent remittent) {
        this.remittent = remittent;

        formattingMetricsHolder.data(FormattingContextNamespaces.REMITTENT_NAMESPACE, remittent);
    }

    void setEntityHandlerContainer(EntityHandlerContainer entityHandlerContainer) {
        this.entityHandlerContainer = entityHandlerContainer;
    }

    void setFormattingMetricsHolder(FormattingMetricsHolder formattingMetricsHolder) {
        this.formattingMetricsHolder = formattingMetricsHolder.copy();
    }

    void setMessage(Object message) {
        this.message = message;
    }

    void setMessageData(ResourceData resourceData) {
        this.resourceData = resourceData;
    }

    void setDefinitiveMessageType(Object definitiveMessageType) {
        this.definitiveMessageType = definitiveMessageType;
    }

    public void setMode(String mode) {
        this.mode = mode;
    }

    private static Object formatMessage(
            MMTSHandler mmtsHandler,
            Object message,
            FormattingMetricsHolder formattingMetricsHolder
    ) {
        if (message instanceof Collection) {
            List messageList = new ArrayList();

            ((Collection) message).forEach(individualMessage -> {
                individualMessage = mmtsHandler.formatString((String) individualMessage, formattingMetricsHolder);
                messageList.add(individualMessage);
            });

            message = messageList;
        } else {
            if (message instanceof String) {
                message = mmtsHandler.formatString((String) message, formattingMetricsHolder);
            } else {
                message = mmtsHandler.tryFormatAndReconstruct(message, formattingMetricsHolder);
            }
        }

        return message;
    }

    private static Object formatMessageData(
            MMTSHandler mmtsHandler,
            Object messageData,
            Object messageType,
            FormattingMetricsHolder formattingMetricsHolder
    ) {
        if (messageData instanceof Collection) {
            List messageList = new ArrayList();

            ((Collection<?>) messageData).forEach(individualMessage -> messageList.add(formatMessageData(mmtsHandler, individualMessage, messageType, formattingMetricsHolder)));

            return messageList;
        }

        ResourceData individualMessage = (ResourceData) messageData;
        if (messageType != null) {
            if (messageType instanceof String) {
                return mmtsHandler.formatData(individualMessage, (String) messageType, formattingMetricsHolder);
            } else {
                return mmtsHandler.formatData(individualMessage, (Class) messageType, formattingMetricsHolder);
            }
        } else {
            return mmtsHandler.formatAbstractResource(individualMessage, formattingMetricsHolder);
        }
    }

    private static void handleMessageDispatch(
            EntityHandlerContainer entityHandlerContainer,
            Object message,
            String mode,
            Object entity
    ) {
        if (message == null) return;

        if (mode == null || mode.isEmpty()) mode = "default";

        if (message instanceof Collection) {
            String finalMode = mode;
            ((Iterable<?>) message).forEach(obj -> sendMessage(entityHandlerContainer, entity, finalMode, obj));
        } else {
            sendMessage(entityHandlerContainer, entity, mode, message);
        }
    }

    private static void sendMessage(
            EntityHandlerContainer entityHandlerContainer,
            Object entity,
            String mode,
            Object message
    ) {
        MessageSender messageSender = entityHandlerContainer.getSender(message.getClass());

        if (messageSender == null) {
            throw new IllegalArgumentException(
                    String.format("unable to find a message sender (entity=%s, message=%s)", entity.getClass(), message.getClass())
            );
        }

        messageSender.sendMessage(entity, mode, message);
    }
}