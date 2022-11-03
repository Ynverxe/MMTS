package com.github.ynverxe.mmts.translation;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.Optional;


/**
 * Finds the {@link ResourceData} of a given path, in the {@link TranslationSource} of a given language.
 *
 * In other words, is the principal translation handler:
 * <ul>
 *     <li>Binds the {@link TranslationSource} to some language.</li>
 *     <li>Provides the {@link ResourceData} from a language source.</li>
 *     <li>Resolves entities languages, see {@link TranslationFinder#resolveLang(Object)}.</li>
 * </ul>
 */
@SuppressWarnings("unused")
public interface TranslationFinder {

    /**
     * Get the translation data in the provided path if it's found or else an empty data.
     *
     * @param entityOrLang - An entity or lang (as {@link String})
     * @param path         - The data path
     * @return the found data or empty if it's the path is empty.
     */
    @NotNull ResourceData getTranslationData(@NotNull Object entityOrLang, @NotNull String path);

    /**
     * Get the translation data in the provided path if it's found or else an empty result.
     *
     * @param path         - The data path
     * @param entityOrLang - An entity or lang (as {@link String})
     * @return the found data or {@link Optional#empty()} if it's the path is empty.
     */
    @NotNull Optional<ResourceData> findTranslationData(@NotNull String path, @NotNull Object entityOrLang);

    /**
     * @param lang - The source lang
     * @return a translation source associated to the provided lang.
     */
    @NotNull Optional<TranslationSource> getTranslationSource(@NotNull String lang);

    /**
     * Finds a {@link Linguist} and delegate the lang resolving to it.
     *
     * @param entity - The entity
     * @return the entity lang.
     *
     * @throws IllegalArgumentException If no {@link Linguist} found or if it returns null.
     */
    @NotNull String resolveLang(@NotNull Object entity) throws IllegalArgumentException;

    /**
     * Bind a {@link Linguist} to resolve entity languages.
     *
     * @param entityClass - The entity class
     * @param linguist - The linguist
     * @param <E> - The entity type
     */
    <E> void bindLinguist(@NotNull Class<E> entityClass, @NotNull Linguist<E> linguist);

    /**
     * Register a {@link TranslationSource} with a lang as identifier.
     *
     * @param lang - The source identifier
     * @param translationSource - The translation source to register
     */
    void addTranslationSource(@NotNull String lang, @NotNull TranslationSource translationSource);

    /**
     * Clear all sources
     */
    void clearSources();

    /**
     * @return an unmodifiable map with the registered sources.
     */
    @NotNull Map<String, TranslationSource> getSources();

    /**
     * @param sourceCreator - The finder source creator.
     * @return a new simple TranslationFinder implementation.
     */
    static @NotNull SimpleTranslationFinder newSimpleTranslationFinder(@Nullable SourceCreator sourceCreator) {
        return new SimpleTranslationFinder(sourceCreator);
    }
}