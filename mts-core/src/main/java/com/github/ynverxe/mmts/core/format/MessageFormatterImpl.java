package com.github.ynverxe.mmts.core.format;

import com.github.ynverxe.mmts.core.exception.NoCreatorFoundException;
import com.github.ynverxe.mmts.core.placeholder.PlaceholderApplier;
import com.github.ynverxe.mmts.core.placeholder.PlaceholderDelimiterPack;
import com.github.ynverxe.mmts.core.placeholder.PlaceholderReplacer;
import com.github.ynverxe.mmts.core.placeholder.PlaceholderReplacerImpl;
import com.github.ynverxe.mmts.core.util.ComposedKey;
import com.github.ynverxe.mmts.core.remittent.Remittent;
import com.github.ynverxe.mmts.translation.MessageData;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

@SuppressWarnings("unchecked, rawtypes")
public class MessageFormatterImpl implements MessageFormatter {

    private final Map<ComposedKey, Object> messageHandlersMap = new HashMap<>();
    private final Map<PlaceholderDelimiterPack, PlaceholderReplacer> placeholderReplacerMap = new HashMap<>();
    private final Map<String, PlaceholderApplier> placeholderApplierMap = new HashMap<>();
    private final Map<String, Class> messageAliases = new HashMap<>();

    private final PlaceholderReplacer DEFAULT_PLACEHOLDER_REPLACER = createPlaceholderReplacer('%', '%');

    public MessageFormatterImpl() {
        addMessageCreator(String.class, new StringMessageExpansion());
    }

    @Override
    public @Nullable Object tryFormatAndReconstruct(
            @NotNull Object object, @Nullable FormattingContext.Configurator contextConfigurator
    ) {
        try {
            MessageData messageData = toMessageData(object);

            if (messageData == null) return null;

            return formatMessage(messageData, object.getClass(), contextConfigurator);
        } catch (NoCreatorFoundException ignored) {}

        return object;
    }

    @Override
    public <T> @NotNull T formatMessage(
            @NotNull MessageData messageData,
            @NotNull Class<T> requiredMessageClass,
            @Nullable FormattingContext.Configurator contextConfigurator
    ) throws NoCreatorFoundException {
        MessageExpansion<T> messageExpansion = (MessageExpansion<T>) messageHandlersMap.get(
                forMessageCreator(Objects.requireNonNull(requiredMessageClass, "messageClass"))
        );

        if (messageExpansion == null) throw new NoCreatorFoundException(requiredMessageClass.toString());

        FormattingContext formattingContext = new FormattingContext(requiredMessageClass);

        if (contextConfigurator != null) contextConfigurator.configure(formattingContext);

        T message = messageExpansion.createNewMessage(
                messageData,
                this,
                formattingContext
        );

        if (String.class != requiredMessageClass) {
            List<FormattingInterceptor<T>> list = (List<FormattingInterceptor<T>>) this.messageHandlersMap.getOrDefault(
                    forMessageInterceptor(Objects.requireNonNull(requiredMessageClass, "messageClass")),
                    Collections.emptyList()
            );

            for (FormattingInterceptor<T> formattingInterceptor : list) {
                message = formattingInterceptor.visit(message, formattingContext);
            }
        }

        return message;
    }

    @Override
    public @NotNull Object formatAbstractMessage(
            @NotNull MessageData messageData,
            FormattingContext.@Nullable Configurator contextConfigurator
    ) throws IllegalArgumentException {
        String messageAlias = messageData.getString("alias");

        if (messageAlias == null)
            throw new IllegalArgumentException("no alias found");

        return formatMessage(messageData, messageAlias, contextConfigurator);
    }

    @Override
    public @NotNull Object formatMessage(
            @NotNull MessageData messageData,
            @NotNull String alias,
            FormattingContext.@Nullable Configurator contextConfigurator
    ) throws IllegalArgumentException {
        Class messageType = messageAliases.get(alias);

        if (messageType == null)
            throw new IllegalArgumentException("no class found with alias: " + alias);

        return formatMessage(messageData, messageType, contextConfigurator);
    }

    @Override
    public @NotNull String formatString(
            @NotNull String str,
            @Nullable FormattingContext.Configurator contextConfigurator
    ) {
        FormattingContext formattingContext = new FormattingContext(String.class);

        if (contextConfigurator != null) {
            contextConfigurator.configure(formattingContext);
        }

        boolean skipPlaceholderFormatting = formattingContext
                .optionalDataGet(FormattingContextNamespaces.SKIP_PLACEHOLDER_APPLICATION, boolean.class)
                .orElse(false);

        if (!skipPlaceholderFormatting) {
            PlaceholderReplacer placeholderReplacer = DEFAULT_PLACEHOLDER_REPLACER;

            PlaceholderDelimiterPack placeholderDelimiterPack =
                    formattingContext.getData(FormattingContextNamespaces.PLACEHOLDER_DELIMITER_NAMESPACE, PlaceholderDelimiterPack.class);

            Remittent remittent = formattingContext.getData(FormattingContextNamespaces.REMITTENT_NAMESPACE, Remittent.class);

            if (remittent != null) {
                if (placeholderDelimiterPack != null) {
                    placeholderReplacer = createPlaceholderReplacer(placeholderDelimiterPack);
                }

                str = placeholderReplacer.replace(remittent.getEntity(), str);
            }
        }

        for (Map.Entry<String, String> entry : formattingContext.getReplacements().entrySet()) {
            String target = entry.getKey();
            String replacement = entry.getValue();

            str = str.replace(target, replacement);
        }

        List<FormattingInterceptor<String>> list = (List<FormattingInterceptor<String>>) this.messageHandlersMap.getOrDefault(
                forMessageInterceptor(String.class),
                Collections.emptyList()
        );

        for (FormattingInterceptor<String> formattingInterceptor : list) {
            str = formattingInterceptor.visit(str, formattingContext);
        }

        return str;
    }

    @Override
    public @Nullable MessageData toMessageData(@NotNull Object obj) {
        Class messageClass = obj.getClass();
        MessageExpansion messageExpansion = (MessageExpansion) messageHandlersMap.get(
                forMessageCreator(messageClass)
        );

        if (messageExpansion == null) throw new NoCreatorFoundException(messageClass.toString());

        return messageExpansion.dismountAsData(obj);
    }

    @Override
    public <T> void addMessageCreator(
            @NotNull Class<T> messageClass, @NotNull MessageExpansion<T> messageExpansion
    ) {
        this.messageHandlersMap.put(
                forMessageCreator(Objects.requireNonNull(messageClass, "messageClass")),
                Objects.requireNonNull(messageExpansion, "messageCreator")
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
    public void addPlaceholderApplier(
            @NotNull String identifier,
            @NotNull PlaceholderApplier placeholderApplier
    ) {
        this.placeholderApplierMap.put(
                Objects.requireNonNull(identifier, "identifier"),
                Objects.requireNonNull(placeholderApplier, "placeholderApplier")
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
                        placeholderApplierMap
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