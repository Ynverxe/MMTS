package com.github.ynverxe.mmts.core;

import com.github.ynverxe.mmts.core.entity.EntityResolver;
import com.github.ynverxe.mmts.core.entity.EntityStrategy;
import com.github.ynverxe.mmts.core.entity.MessageSender;
import com.github.ynverxe.mmts.core.exception.NoCreatorFoundException;
import com.github.ynverxe.mmts.core.format.MessageExpansion;
import com.github.ynverxe.mmts.core.format.FormattingContext;
import com.github.ynverxe.mmts.core.format.FormattingInterceptor;
import com.github.ynverxe.mmts.core.format.MessageFormatter;
import com.github.ynverxe.mmts.core.message.SimpleConfigurableMessage;
import com.github.ynverxe.mmts.core.message.TranslatingConfigurableMessage;
import com.github.ynverxe.mmts.core.placeholder.PlaceholderValueProvider;
import com.github.ynverxe.mmts.core.placeholder.PlaceholderDelimiterPack;
import com.github.ynverxe.mmts.core.placeholder.PlaceholderReplacer;
import com.github.ynverxe.mts.common.HierarchyMapSearchUtil;
import com.github.ynverxe.mmts.translation.AbstractTranslationFinder;
import com.github.ynverxe.mmts.translation.Linguist;
import com.github.ynverxe.mmts.translation.SourceCreator;
import com.github.ynverxe.mmts.translation.MessageData;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

@SuppressWarnings("unchecked, rawtypes")
public class MMTSHandlerImpl extends AbstractTranslationFinder implements MMTSHandler {

    private final MessageFormatter messageFormatter;
    private final Map<Class, EntityHandlerContainer> entityHandlerContainerMap = new HashMap<>();

    public MMTSHandlerImpl(SourceCreator sourceCreator) {
        super(sourceCreator);
        this.messageFormatter = MessageFormatter.newMessageFormatter();
    }

    @Override
    public @NotNull Optional<?> translateAndFormat(@NotNull Object langOrEntity, @NotNull String path, @NotNull Class<?> messageClass, FormattingContext.@Nullable Configurator contextConfigurator) {
        return findTranslationData(path, langOrEntity)
                .map(translationData -> formatMessage(translationData, messageClass, contextConfigurator));
    }

    @Override
    public @NotNull Optional<?> translateAndFormat(@NotNull Object langOrEntity, @NotNull String path, FormattingContext.@Nullable Configurator contextConfigurator) {
        return findTranslationData(path, langOrEntity)
                .map(translationData -> formatAbstractMessage(translationData, contextConfigurator));
    }

    @Override
    public @NotNull <E> EntityHandlerContainer<E> getContainerFor(@NotNull Class<E> entityClass) {
        return entityHandlerContainerMap.computeIfAbsent(entityClass, (k) -> new EntityHandlerContainerImpl());
    }

    @Override
    public @Nullable <E> EntityHandlerContainer<E> findContainerByHierarchy(@NotNull Class<?> entityClass) {
        EntityHandlerContainer<E> entityHandlerContainer = entityHandlerContainerMap.get(entityClass);

        if (entityHandlerContainer != null) return entityHandlerContainer;

        return HierarchyMapSearchUtil.findBySuperclasses(entityClass, entityHandlerContainerMap);
    }

    @Override
    public @NotNull Object tryResolveEntity(@NotNull Object entity) {
        Class entityClass = entity.getClass();
        EntityHandlerContainer entityHandlerContainer = findContainerByHierarchy(entityClass);

        if (entityHandlerContainer != null) {
            EntityResolver resolver = entityHandlerContainer.getResolver();

            if (resolver != null) {
                return tryResolveEntity(resolver.resolveEntity(entity));
            }
        }

        return entity;
    }

    @Override
    public @NotNull SimpleConfigurableMessage prepareMessage(@NotNull Object message) {
        return new SimpleConfigurableMessage(this, Objects.requireNonNull(message, "message"));
    }

    @Override
    public @NotNull TranslatingConfigurableMessage translating(@NotNull String path, @NotNull Class<?> messageClass) {
        return new TranslatingConfigurableMessage(
                this,
                Objects.requireNonNull(path, "path"),
                Objects.requireNonNull(messageClass, "messageClass")
        );
    }

    @Override
    public @NotNull TranslatingConfigurableMessage translatingAbstractMessage(@NotNull String path) {
        return new TranslatingConfigurableMessage(
                this,
                Objects.requireNonNull(path, "path"),
                null
        );
    }

    @Override
    public @Nullable Object tryFormatAndReconstruct(@NotNull Object object, FormattingContext.@Nullable Configurator contextConfigurator) {
        return messageFormatter.tryFormatAndReconstruct(object, contextConfigurator);
    }

    @Override
    public @NotNull Object formatAbstractMessage(@NotNull MessageData messageData, FormattingContext.@Nullable Configurator contextConfigurator) throws IllegalArgumentException {
        return messageFormatter.formatAbstractMessage(messageData, contextConfigurator);
    }

    @Override
    public @NotNull Object formatMessage(@NotNull MessageData messageData, @NotNull String alias, FormattingContext.@Nullable Configurator contextConfigurator) throws IllegalArgumentException {
        return messageFormatter.formatMessage(messageData, alias, contextConfigurator);
    }

    @Override
    public <T> @NotNull T formatMessage(@NotNull MessageData messageData, @NotNull Class<T> requiredMessageClass, FormattingContext.@Nullable Configurator contextConfigurator) throws NoCreatorFoundException {
        return messageFormatter.formatMessage(messageData, requiredMessageClass, contextConfigurator);
    }

    @Override
    public @NotNull String formatString(@NotNull String str, FormattingContext.@Nullable Configurator contextConfigurator) {
        return messageFormatter.formatString(str, contextConfigurator);
    }

    @Override
    public @Nullable MessageData toMessageData(@NotNull Object obj) {
        return messageFormatter.toMessageData(obj);
    }

    @Override
    public <T> void addMessageCreator(@NotNull Class<T> messageClass, @NotNull MessageExpansion<T> messageExpansion) {
        messageFormatter.addMessageCreator(messageClass, messageExpansion);
    }

    @Override
    public <T> void addFormattingVisitor(@NotNull Class<T> messageClass, @NotNull FormattingInterceptor<T> formattingInterceptor) {
        messageFormatter.addFormattingVisitor(messageClass, formattingInterceptor);
    }

    @Override
    public void addPlaceholderValueProvider(@NotNull String identifier, @NotNull PlaceholderValueProvider placeholderValueProvider) {
        messageFormatter.addPlaceholderValueProvider(identifier, placeholderValueProvider);
    }

    @Override
    public @NotNull PlaceholderReplacer createPlaceholderReplacer(char startDelimiter, char endDelimiter) {
        return messageFormatter.createPlaceholderReplacer(startDelimiter, endDelimiter);
    }

    @Override
    public @NotNull PlaceholderReplacer createPlaceholderReplacer(@NotNull PlaceholderDelimiterPack placeholderDelimiterPack) {
        return messageFormatter.createPlaceholderReplacer(placeholderDelimiterPack);
    }

    @Override
    public void bindMessageTypeAlias(@NotNull String alias, @NotNull Class<?> messageType) {
        messageFormatter.bindMessageTypeAlias(alias, messageType);
    }

    @Override
    public @NotNull String resolveLang(@NotNull Object entity) throws IllegalArgumentException {
        EntityHandlerContainer entityHandlerContainer = findContainerByHierarchy(entity.getClass());

        Class entityClass = entity.getClass();

        if (entityHandlerContainer == null)
            throw new IllegalArgumentException("no container found for: " + entityClass);

        Linguist linguist = entityHandlerContainer.getLinguist();

        if (linguist == null)
            throw new IllegalArgumentException("no linguist found for: " + entityClass);

        String lang = linguist.resolveLanguage(entity);

        if (lang == null)
            throw new IllegalArgumentException("unable to resolve the language of entity: " + entityClass);

        return lang;
    }

    @Override
    public <E> void bindLinguist(@NotNull Class<E> entityClass, @NotNull Linguist<E> linguist) {
        EntityHandlerContainer entityHandlerContainer = getContainerFor(entityClass);

        entityHandlerContainer.bindLinguist(linguist);
    }

    static class EntityHandlerContainerImpl<E> implements EntityHandlerContainer<E> {

        private final List<MessageSender<E>> messageSenders = new ArrayList<>();
        private EntityStrategy<E> entityStrategy;
        private Linguist<E> linguist;
        private EntityResolver<E, ?> resolver;

        @Override
        public @NotNull EntityHandlerContainer<E> bindSender(@NotNull MessageSender<E> messageSender) {
            Objects.requireNonNull(messageSender, "messageSender");

            if (messageSenders.contains(messageSender)) throw new UnsupportedOperationException("sender is already registered");

            messageSenders.add(messageSender);

            return this;
        }

        @Override
        public @NotNull EntityHandlerContainer<E> bindStrategy(@NotNull EntityStrategy<E> strategy) {
            this.entityStrategy = Objects.requireNonNull(strategy, "entityStrategy");

            return this;
        }

        @Override
        public @NotNull EntityHandlerContainer<E> bindLinguist(@NotNull Linguist<E> linguist) {
            this.linguist = Objects.requireNonNull(linguist, "linguist");

            return this;
        }

        @Override
        public @NotNull <T> EntityHandlerContainer<E> bindResolver(@NotNull EntityResolver<E, T> resolver) {
            this.resolver = Objects.requireNonNull(resolver, "resolver");

            return this;
        }

        @Override
        public @Nullable MessageSender<E> getSender(@NotNull Class<?> messageClass) {
            for (MessageSender<E> messageSender : messageSenders) {
                if (messageSender.offer(messageClass)) return messageSender;
            }

            return null;
        }

        @Override
        public @Nullable EntityStrategy<E> getStrategy() {
            return entityStrategy;
        }

        @Override
        public @Nullable Linguist<E> getLinguist() {
            return linguist;
        }

        @Override
        public @Nullable <T> EntityResolver<E, T> getResolver() {
            return (EntityResolver<E, T>) resolver;
        }
    }
}