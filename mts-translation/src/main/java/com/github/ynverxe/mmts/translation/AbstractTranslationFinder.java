package com.github.ynverxe.mmts.translation;

import com.github.ynverxe.data.DataNode;
import org.jetbrains.annotations.NotNull;

import java.util.*;

@SuppressWarnings("unused")
public abstract class AbstractTranslationFinder implements TranslationFinder {

    private final Map<String, TranslationSource> translationSourceMap = new HashMap<>();
    private final SourceCreator sourceCreator;

    protected AbstractTranslationFinder(SourceCreator sourceCreator) {
        this.sourceCreator = sourceCreator;
    }

    @Override
    public @NotNull MessageData getTranslationData(@NotNull Object entityOrLang, @NotNull String path) {
        return findTranslationData(path, entityOrLang)
                .orElse(MessageData.createMessageData(path, null));
    }

    @Override
    @SuppressWarnings("unchecked")
    public @NotNull Optional<MessageData> findTranslationData(@NotNull String path, @NotNull Object entityOrLang) {
        Objects.requireNonNull(path, "path");

        String lang = entityOrLang instanceof String ? (String) entityOrLang : resolveLang(entityOrLang);

        TranslationSource source = translationSourceMap.get(lang);

        if (source == null) {
            TranslationSource created = sourceCreator != null ? sourceCreator.createSource(lang) : null;

            if (created == null) return Optional.empty();

            source = created;
            translationSourceMap.put(lang, source);
        }

        Object found = source.findData(path);

        if (found == null) return Optional.empty();

        Map<String, Object> map;
        if (found instanceof Map) {
            map = (Map<String, Object>) found;
        } else {
            map = Collections.singletonMap("value", found);
        }

        return Optional.of(MessageData.createMessageData(path, DataNode.fromMap(map)));
    }

    @Override
    public @NotNull Optional<TranslationSource> getTranslationSource(@NotNull String lang) {
        return Optional.ofNullable(translationSourceMap.get(lang));
    }

    public @NotNull AbstractTranslationFinder bindLangTranslationSource(
            @NotNull String lang,
            @NotNull TranslationSource translationSource
    ) {
        translationSourceMap.put(
                Objects.requireNonNull(lang, "lang"),
                Objects.requireNonNull(translationSource, "translationSource")
        );

        return this;
    }

    @Override
    public void clearSources() {
        this.translationSourceMap.clear();
    }

    @Override
    public void addTranslationSource(@NotNull String lang, @NotNull TranslationSource translationSource) {
        this.translationSourceMap.put(Objects.requireNonNull(lang, "lang"), Objects.requireNonNull(translationSource, "translationSource"));
    }

    @Override
    public @NotNull Map<String, TranslationSource> getSources() {
        return Collections.unmodifiableMap(translationSourceMap);
    }
}