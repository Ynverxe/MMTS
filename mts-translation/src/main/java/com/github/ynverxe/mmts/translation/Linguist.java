package com.github.ynverxe.mmts.translation;

import org.jetbrains.annotations.Nullable;

/**
 * Takes care of resolve and providing the entity language.
 *
 * @param <E> The entity type
 */
public interface Linguist<E> {

    /**
     * @param entity - The entity to resolve
     * @return the entity language or null if it could not be found.
     */
    @Nullable String resolveLanguage(E entity);

}