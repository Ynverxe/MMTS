package com.github.ynverxe.mmts.translation;

import com.github.ynverxe.data.DataNode;
import com.github.ynverxe.data.FunctionalDataContainer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

/**
 * Contains the message data. Is not mutable.
 */
@SuppressWarnings("unused")
public final class MessageData implements FunctionalDataContainer<String> {

    private final String path;
    private final DataNode dataNode;
    private final Map<String, Object> dataMap;

    private MessageData(String path, DataNode dataNode) {
        this.path = path;
        this.dataNode = dataNode;
        this.dataMap = dataNode.simplify();
    }

    /**
     * @return the first entry of the data or empty if the data is empty.
     */
    public @NotNull Optional<Map.Entry<String, Object>> firstEntry() {
        Iterator<Map.Entry<String, Object>> iterator = dataMap.entrySet().iterator();

        if (iterator.hasNext())
            return Optional.of(iterator.next());

        return Optional.empty();
    }

    /**
     * @return the first value of the data or empty if the data is empty.
     */
    public @NotNull Optional<Object> firstValueOrEmpty() {
        return firstEntry().map(Map.Entry::getValue);
    }

    /**
     * @return the first key of the data or empty if the data is empty.
     */
    public @NotNull Optional<String> firstKeyOrEmpty() {
        return firstEntry().map(Map.Entry::getKey);
    }

    /**
     * @return an unmodifiable map with the current data.
     */
    public @NotNull Map<String, Object> getDataMap() {
        return Collections.unmodifiableMap(dataMap);
    }

    /**
     * @return the current path (can be null)
     */
    public @Nullable String getPath() {
        return path;
    }

    /**
     * Create a new message data.
     *
     * @param path - The path where the data comes from.
     * @param dataNode - The data.
     * @return a new instance of this class.
     */
    public static @NotNull MessageData createMessageData(@Nullable String path, @Nullable DataNode dataNode) {
        return new MessageData(path, dataNode != null ? dataNode : DataNode.EMPTY);
    }

    /**
     * Create a new message data without path.
     *
     * @param dataNode - The data.
     * @return a new instance of this class.
     */
    public static @NotNull MessageData withoutPath(@Nullable DataNode dataNode) {
        return createMessageData(null, dataNode);
    }

    @Override
    public @Nullable Object get(String s) {
        return dataNode.get(s);
    }

    @Override
    public boolean has(String s) {
        return dataNode.has(s);
    }

    @Override
    public boolean identify(String s, Class<?> aClass) {
        return dataNode.identify(s, aClass);
    }
}