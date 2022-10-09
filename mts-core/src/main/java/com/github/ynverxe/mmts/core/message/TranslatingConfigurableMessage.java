package com.github.ynverxe.mmts.core.message;

import com.github.ynverxe.mmts.core.EntityHandlerContainer;
import com.github.ynverxe.mmts.core.entity.EntityStrategy;
import com.github.ynverxe.mmts.core.MMTSHandler;
import com.github.ynverxe.mmts.core.format.FormattingContextNamespaces;
import com.github.ynverxe.mmts.core.format.FormattingContext;
import com.github.ynverxe.mmts.translation.Linguist;
import com.github.ynverxe.mmts.translation.TranslationData;
import com.github.ynverxe.mmts.core.remittent.Remittent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

@SuppressWarnings("rawtypes, unchecked, unused")
public class TranslatingConfigurableMessage extends AbstractConfigurableMessage {

    private final String path;
    private final Class messageClass;
    private Object defaultMessage;

    public TranslatingConfigurableMessage(
            MMTSHandler mmtsHandler,
            String path,
            Class messageClass
    ) {
        super(mmtsHandler);
        this.path = path;
        this.messageClass = messageClass;
    }

    public @NotNull TranslatingConfigurableMessage defaultMessage(@NotNull Object def) {
        this.defaultMessage = Objects.requireNonNull(def, "def cannot be null");

        return this;
    }

    @Override
    protected @Nullable <E> Object findMessage(@NotNull Remittent remittent, EntityHandlerContainer<E> entityHandlerContainer) {
        String lang = remittent.getLang();

        //noinspection ConstantConditions
        TranslationData translationData = mmtsHandler.getTranslationData(lang, path);

        if (translationData.getDataMap().isEmpty()) {
            if (defaultMessage instanceof TranslationData) {
                translationData = (TranslationData) defaultMessage;
            }

            if (defaultMessage == null || translationData.getDataMap().isEmpty()) {
                performEntityStrategy(remittent.getEntity(), entityHandlerContainer);
            }
        }

        return translationData.getDataMap().isEmpty() ? defaultMessage : translationData;
    }

    @Override
    protected <E> Object formatMessage(
            @NotNull Remittent remittent,
            @NotNull EntityHandlerContainer<E> entityHandlerContainer,
            @NotNull Object message
    ) throws RuntimeException {
        FormattingContext.Configurator contextConfigurator = context -> {
            context.addData(FormattingContextNamespaces.REMITTENT_NAMESPACE, remittent);
            configurator.configure(context);
        };

        if (message instanceof Collection) {
            List messageList = new ArrayList();

            ((Collection<?>) message).forEach(individualMessage -> messageList.add(formatMessage(remittent, entityHandlerContainer, message)));

            return messageList;
        }

        if (message instanceof TranslationData) {
            TranslationData translationData = (TranslationData) message;

            Class messageClass = this.messageClass;

            if (!translationData.getDataMap().isEmpty()) {
                if (messageClass != null) {
                    return mmtsHandler.formatMessage(translationData, messageClass, contextConfigurator);
                } else {
                    return mmtsHandler.formatAbstractMessage(translationData, contextConfigurator);
                }
            }
        } else {
            Object formatted = super.formatMessage(remittent, entityHandlerContainer, message);
            if (formatted != message) {
                return formatted;
            }
        }

        return null;
    }

    @Override
    protected Remittent newRemittent(@NotNull Object entity, @NotNull EntityHandlerContainer entityHandlerContainer) {
        Class entityClass = entity.getClass();
        Linguist linguist = entityHandlerContainer.getLinguist();

        if (linguist == null) throw new RuntimeException("No linguist found for " + entityClass);

        String lang = linguist.resolveLanguage(entity);

        if (lang == null) throw new RuntimeException("Unable to get lang of entity: " + entityClass);

        return Remittent.newRemittent(entity, lang);
    }

    private void performEntityStrategy(Object entity, EntityHandlerContainer entityHandlerContainer) {
        EntityStrategy entityStrategy = entityHandlerContainer.getStrategy();

        if (entityStrategy == null)
            throw new RuntimeException("No strategy found for: " + entity.getClass());

        entityStrategy.onNoMessageFound(mmtsHandler, entity, path);
    }
}