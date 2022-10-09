package com.github.ynverxe.mmts.translation;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 *
 * When the {@link TranslationFinder} not found any {@link TranslationSource}
 * for a given language, it calls a {@link SourceCreator} (if it has)
 * to create the missing {@link TranslationSource}.
 *
 *
 * <pre>{@code
 * TranslationSource source = translationSourceMap.get(lang);
 *
 * if (source == null) {
 *   TranslationSource created = sourceCreator != null ? sourceCreator.createSource(lang) : null;
 *
 *   if (created == null) return Optional.empty();
 *
 *   source = created;
 *   translationSourceMap.put(lang, source);
 *}
 * }
 * </pre>
 *
 */
public interface SourceCreator {

    /**
     * Create a new {@link TranslationSource} for lang.
     *
     * @param lang - The lang identifier.
     * @return a new {@link TranslationSource} or null if something failed.
     */
    @Nullable TranslationSource createSource(@NotNull String lang);

}