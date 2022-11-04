package com.github.ynverxe.mmts.core.impl;

import com.github.ynverxe.mmts.core.exception.NoCreatorFoundException;
import com.github.ynverxe.mmts.core.format.*;
import com.github.ynverxe.mmts.core.placeholder.PlaceholderValueProvider;
import com.github.ynverxe.mmts.core.placeholder.PlaceholderDelimiterPack;
import com.github.ynverxe.mmts.core.placeholder.PlaceholderReplacer;
import com.github.ynverxe.mmts.core.placeholder.PlaceholderReplacerImpl;
import com.github.ynverxe.mmts.core.util.ComposedKey;
import com.github.ynverxe.mmts.core.remittent.Remittent;
import com.github.ynverxe.mmts.translation.ResourceData;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

@SuppressWarnings("unchecked, rawtypes")
public class ObjectFormatterImpl implements ObjectFormatter {

    private final Map<ComposedKey, Object> messageHandlersMap = new HashMap<>();
    private final Map<PlaceholderDelimiterPack, PlaceholderReplacer> placeholderReplacerMap = new HashMap<>();
    private final Map<String, PlaceholderValueProvider> placeholderValueProviderMap = new HashMap<>();
    private final Map<String, Class> messageAliases = new HashMap<>();

    private final PlaceholderReplacer DEFAULT_PLACEHOLDER_REPLACER = createPlaceholderReplacer('%', '%');

    public ObjectFormatterImpl() {
        addMessageCreator(String.class, new StringExpansion());
    }

    @Override
    public @Nullable Object tryFormatAndReconstruct(
            @NotNull Object object, @Nullable FormattingMetricsHolder formattingMetricsHolder
    ) {
        try {
            ResourceData resourceData = toResourceData(object);

            if (resourceData == null) return null;

            return formatData(resourceData, object.getClass(), formattingMetricsHolder);
        } catch (NoCreatorFoundException ignored) {}

        return object;
    }

    @Override
    public <T> @NotNull T formatData(
            @NotNull ResourceData resourceData,
            @NotNull Class<T> requiredMessageClass,
            @Nullable FormattingMetricsHolder formattingMetricsHolder
    ) throws NoCreatorFoundException {
        ObjectExpansion<T> objectExpansion = (ObjectExpansion<T>) messageHandlersMap.get(
                forMessageCreator(Objects.requireNonNull(requiredMessageClass, "messageClass"))
        );

        if (objectExpansion == null) throw new NoCreatorFoundException(requiredMessageClass.toString());

        formattingMetricsHolder = formattingMetricsHolder != null ? formattingMetricsHolder.copy() : FormattingMetricsHolder.create();

        T message = objectExpansion.createNewMessage(
                resourceData,
                this,
                formattingMetricsHolder
        );

        if (String.class != requiredMessageClass) {
            List<FormattingInterceptor<T>> list = (List<FormattingInterceptor<T>>) this.messageHandlersMap.getOrDefault(
                    forMessageInterceptor(Objects.requireNonNull(requiredMessageClass, "messageClass")),
                    Collections.emptyList()
            );

            for (FormattingInterceptor<T> formattingInterceptor : list) {
                message = formattingInterceptor.visit(message, formattingMetricsHolder);
            }
        }

        return message;
    }

    @Override
    public @NotNull Object formatAbstractResource(
            @NotNull ResourceData resourceData,
            @Nullable FormattingMetricsHolder formattingMetricsHolder
    ) throws IllegalArgumentException {
        String messageAlias = resourceData.getString("alias");

        if (messageAlias == null)
            throw new IllegalArgumentException("no alias found");

        return formatData(resourceData, messageAlias, formattingMetricsHolder);
    }

    @Override
    public @NotNull Object formatData(
            @NotNull ResourceData resourceData,
            @NotNull String alias,
            @Nullable FormattingMetricsHolder formattingMetricsHolder
    ) throws IllegalArgumentException {
        Class messageType = messageAliases.get(alias);

        if (messageType == null)
            throw new IllegalArgumentException("no class found with alias: " + alias);

        return formatData(resourceData, messageType, formattingMetricsHolder);
    }

    @Override
    public @NotNull String formatString(
            @NotNull String str,
            @Nullable FormattingMetricsHolder formattingMetricsHolder
    ) {
        formattingMetricsHolder = formattingMetricsHolder != null ? formattingMetricsHolder.copy() : FormattingMetricsHolder.create();

        boolean skipPlaceholderFormatting = formattingMetricsHolder
                .optionalDataGet(FormattingContextNamespaces.SKIP_PLACEHOLDER_APPLICATION, boolean.class)
                .orElse(false);

        if (!skipPlaceholderFormatting) {
            PlaceholderReplacer placeholderReplacer = DEFAULT_PLACEHOLDER_REPLACER;

            PlaceholderDelimiterPack placeholderDelimiterPack =
                    formattingMetricsHolder.findData(FormattingContextNamespaces.PLACEHOLDER_DELIMITER, PlaceholderDelimiterPack.class);

            Remittent remittent = formattingMetricsHolder.findData(FormattingContextNamespaces.REMITTENT_NAMESPACE, Remittent.class);

            if (remittent != null) {
                if (placeholderDelimiterPack != null) {
                    placeholderReplacer = createPlaceholderReplacer(placeholderDelimiterPack);
                }

                str = placeholderReplacer.replace(remittent.getEntity(), str);
            }
        }

        List<FormattingInterceptor<String>> list = (List<FormattingInterceptor<String>>) this.messageHandlersMap.getOrDefault(
                forMessageInterceptor(String.class),
                Collections.emptyList()
        );

        for (FormattingInterceptor<String> formattingInterceptor : list) {
            str = formattingInterceptor.visit(str, formattingMetricsHolder);
        }

        return str;
    }

    @Override
    public @Nullable ResourceData toResourceData(@NotNull Object obj) {
        Class messageClass = obj.getClass();
        ObjectExpansion objectExpansion = (ObjectExpansion) messageHandlersMap.get(
                forMessageCreator(messageClass)
        );

        if (objectExpansion == null) throw new NoCreatorFoundException(messageClass.toString());

        return objectExpansion.dismountAsData(obj);
    }

    @Override
    public <T> void addMessageCreator(
            @NotNull Class<T> messageClass, @NotNull ObjectExpansion<T> objectExpansion
    ) {
        this.messageHandlersMap.put(
                forMessageCreator(Objects.requireNonNull(messageClass, "messageClass")),
                Objects.requireNonNull(objectExpansion, "messageCreator")
        );
    }

    @Override
    public <T> void addFormattingVisitor(
            @NotNull Class<T> messageClass,
            @NotNull FormattingInterceptor<T> formattingInterceptor
    ) {
        Objects.requireNonNull(formattingInterceptor, "formattingInterceptor");

        List list = (List) this.messageHandlersMap.computeIfAbsent(
                forMessageInterceptor(Objects.requireNonNull(messageClass, "messageClass")),
                (k) -> new ArrayList<>()
        );

        list.add(formattingInterceptor);
    }

    @Override
    public void addPlaceholderValueProvider(
            @NotNull String identifier,
            @NotNull PlaceholderValueProvider placeholderValueProvider
    ) {
        this.placeholderValueProviderMap.put(
                Objects.requireNonNull(identifier, "identifier"),
                Objects.requireNonNull(placeholderValueProvider, "placeholderApplier")
        );
    }

    @Override
    public @NotNull PlaceholderReplacer createPlaceholderReplacer(char startDelimiter, char endDelimiter) {
        return createPlaceholderReplacer(new PlaceholderDelimiterPack(startDelimiter, endDelimiter));
    }

    @Override
    public @NotNull PlaceholderReplacer createPlaceholderReplacer(@NotNull PlaceholderDelimiterPack placeholderDelimiterPack) {
        return placeholderReplacerMap.computeIfAbsent(
                placeholderDelimiterPack,
                (k) -> new PlaceholderReplacerImpl(
                        placeholderDelimiterPack.getStartDelimiter(),
                        placeholderDelimiterPack.getEndDelimiter(),
                        placeholderValueProviderMap
                )
        );
    }

    @Override
    public void bindMessageTypeAlias(@NotNull String alias, @NotNull Class<?> messageType) {
        messageAliases.put(Objects.requireNonNull(alias, "alias"), Objects.requireNonNull(messageType, "messageType"));
    }

    private static ComposedKey forMessageCreator(Class messageClass) {
        return new ComposedKey(messageClass, "message-creator");
    }

    private static ComposedKey forMessageInterceptor(Class messageClass) {
        return new ComposedKey(messageClass, "message-interceptor");
    }
}