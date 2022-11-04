package com.github.ynverxe.mmts.core.impl;

import com.github.ynverxe.mmts.core.EntityHandlerContainer;
import com.github.ynverxe.mmts.core.MMTSHandler;
import com.github.ynverxe.mmts.core.entity.EntityResolver;
import com.github.ynverxe.mmts.core.entity.EntityStrategy;
import com.github.ynverxe.mmts.core.entity.MessageSender;
import com.github.ynverxe.mmts.core.exception.NoCreatorFoundException;
import com.github.ynverxe.mmts.core.format.FormattingMetricsHolder;
import com.github.ynverxe.mmts.core.format.ObjectExpansion;
import com.github.ynverxe.mmts.core.format.FormattingInterceptor;
import com.github.ynverxe.mmts.core.format.ObjectFormatter;
import com.github.ynverxe.mmts.core.format.def.ReplacementValuesApplier;
import com.github.ynverxe.mmts.core.message.MessagingResource;
import com.github.ynverxe.mmts.core.message.Messenger;
import com.github.ynverxe.mmts.core.resource.FindableResource;
import com.github.ynverxe.mmts.core.placeholder.PlaceholderValueProvider;
import com.github.ynverxe.mmts.core.placeholder.PlaceholderDelimiterPack;
import com.github.ynverxe.mmts.core.placeholder.PlaceholderReplacer;
import com.github.ynverxe.mts.common.HierarchyMapSearchUtil;
import com.github.ynverxe.mmts.translation.AbstractTranslationFinder;
import com.github.ynverxe.mmts.translation.Linguist;
import com.github.ynverxe.mmts.translation.SourceCreator;
import com.github.ynverxe.mmts.translation.ResourceData;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

@SuppressWarnings("unchecked, rawtypes")
public class MMTSHandlerImpl extends AbstractTranslationFinder implements MMTSHandler {

    private final Messenger messenger;
    private final ObjectFormatter objectFormatter;
    private final Map<Class, EntityHandlerContainer> entityHandlerContainerMap = new HashMap<>();

    public MMTSHandlerImpl(SourceCreator sourceCreator) {
        super(sourceCreator);
        this.objectFormatter = ObjectFormatter.newObjectFormatter();
        this.messenger = new MessengerImpl(this);
        addFormattingVisitor(String.class, new ReplacementValuesApplier(this));
    }

    @Override
    public @NotNull Map<Object, Object> processModel(
            @NotNull FindableResource model,
            @NotNull Object... entitiesOrLanguages
    ) {
        String path = model.path();
        Map<String, ResourceData> cachedData = new HashMap<>();
        Map<Object, Object> finalValuesByEntityOrLang = new HashMap<>();

        for (Object entityOrLanguage : entitiesOrLanguages) {
            String lang = entityOrLanguage instanceof String ? (String) entityOrLanguage : resolveLang(entityOrLanguage);

            ResourceData resourceData = cachedData.get(lang);

            if (resourceData == null) {
                cachedData.put(lang, resourceData = getTranslationData(lang, path));
            }

            if (resourceData.getDataMap().isEmpty()) continue;

            Object value;
            if (model.abstractValue()) {
                value = formatAbstractResource(resourceData, model);
            } else {
                String alias = model.typeName();
                if (alias != null) {
                    value = formatData(resourceData, alias, model);
                } else {
                    //noinspection ConstantConditions
                    value = formatData(resourceData, model.messageClass(), model);
                }
            }

            finalValuesByEntityOrLang.put(entityOrLanguage, value);
        }

        return finalValuesByEntityOrLang;
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
    public @NotNull Object formatData(@NotNull ResourceData resourceData, @NotNull String alias, @Nullable FormattingMetricsHolder formattingMetricsHolder) throws IllegalArgumentException {
        return objectFormatter.formatData(resourceData, alias, formattingMetricsHolder);
    }

    @Override
    public @Nullable Object tryFormatAndReconstruct(@NotNull Object object, @Nullable FormattingMetricsHolder formattingMetricsHolder) {
        return objectFormatter.tryFormatAndReconstruct(object, formattingMetricsHolder);
    }

    @Override
    public @NotNull Object formatAbstractResource(@NotNull ResourceData resourceData, @Nullable FormattingMetricsHolder formattingMetricsHolder) throws IllegalArgumentException {
        return objectFormatter.formatAbstractResource(resourceData, formattingMetricsHolder);
    }

    @Override
    public <T> @NotNull T formatData(@NotNull ResourceData resourceData, @NotNull Class<T> requiredMessageClass, @Nullable FormattingMetricsHolder formattingMetricsHolder) throws NoCreatorFoundException {
        return objectFormatter.formatData(resourceData, requiredMessageClass, formattingMetricsHolder);
    }

    @Override
    public @NotNull String formatString(@NotNull String str, @Nullable FormattingMetricsHolder formattingMetricsHolder) {
        return objectFormatter.formatString(str, formattingMetricsHolder);
    }

    @Override
    public @Nullable ResourceData toResourceData(@NotNull Object obj) {
        return objectFormatter.toResourceData(obj);
    }

    @Override
    public <T> void addMessageCreator(@NotNull Class<T> messageClass, @NotNull ObjectExpansion<T> objectExpansion) {
        objectFormatter.addMessageCreator(messageClass, objectExpansion);
    }

    @Override
    public <T> void addFormattingVisitor(@NotNull Class<T> messageClass, @NotNull FormattingInterceptor<T> formattingInterceptor) {
        objectFormatter.addFormattingVisitor(messageClass, formattingInterceptor);
    }

    @Override
    public void addPlaceholderValueProvider(@NotNull String identifier, @NotNull PlaceholderValueProvider placeholderValueProvider) {
        objectFormatter.addPlaceholderValueProvider(identifier, placeholderValueProvider);
    }

    @Override
    public @NotNull PlaceholderReplacer createPlaceholderReplacer(char startDelimiter, char endDelimiter) {
        return objectFormatter.createPlaceholderReplacer(startDelimiter, endDelimiter);
    }

    @Override
    public @NotNull PlaceholderReplacer createPlaceholderReplacer(@NotNull PlaceholderDelimiterPack placeholderDelimiterPack) {
        return objectFormatter.createPlaceholderReplacer(placeholderDelimiterPack);
    }

    @Override
    public void bindMessageTypeAlias(@NotNull String alias, @NotNull Class<?> messageType) {
        objectFormatter.bindMessageTypeAlias(alias, messageType);
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

    @Override
    public void dispatchMessage(@NotNull Object message, @Nullable String mode, @NotNull Object entityOrEntities, Object... replacements) {
        messenger.dispatchMessage(message, mode, entityOrEntities, replacements);
    }

    @Override
    public void dispatchMessage(@NotNull Object message, @Nullable String mode, @NotNull Object entityOrEntities, @NotNull FormattingMetricsHolder formattingMetricsHolder, Object... replacements) {
        messenger.dispatchMessage(message, mode, entityOrEntities, formattingMetricsHolder, replacements);
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