package com.github.ynverxe.mmts.translation;

import com.github.ynverxe.mmts.common.HierarchyMapSearchUtil;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@SuppressWarnings("unchecked, rawtypes, unused")
public class SimpleTranslationFinder extends AbstractTranslationFinder {

    private final Map<Class, Linguist> linguistMap = new HashMap<>();

    protected SimpleTranslationFinder(SourceCreator sourceCreator) {
        super(sourceCreator);
    }

    @Override
    public @NotNull String resolveLang(@NotNull Object entity) throws IllegalArgumentException {
        Class entityClass = entity.getClass();

        Linguist linguist = linguistMap.get(entityClass);

        if (linguist == null) {
            linguist = HierarchyMapSearchUtil.findBySuperclasses(entityClass, linguistMap);
        }

        if (linguist == null)
            throw new IllegalArgumentException("no linguist found for: " + entityClass);

        String lang = linguist.resolveLanguage(entity);

        if (lang == null)
            throw new IllegalArgumentException("unable to resolve the language of entity: " + entityClass);

        return lang;
    }

    @Override
    public <E> void bindLinguist(@NotNull Class<E> entityClass, @NotNull Linguist<E> linguist) {
        this.linguistMap.put(Objects.requireNonNull(entityClass, "entityClass"), Objects.requireNonNull(linguist, "linguist"));
    }
}