package com.github.ynverxe.mmts.core.resource;

import com.github.ynverxe.mmts.core.format.FormattingMetricsHolder;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

public class FindableResource extends FormattingMetricsHolder {

    private final String typeName;
    private final Class<?> messageClass;
    private final String path;
    private final boolean abstractValue;

    protected FindableResource(String typeName, Class<?> messageClass, String path, boolean abstractValue) {
        this.typeName = typeName;
        this.messageClass = messageClass;
        this.path = path;
        this.abstractValue = abstractValue;
    }

    public @Nullable Class<?> messageClass() {
        return messageClass;
    }

    public @Nullable String typeName() {
        return typeName;
    }

    public @NotNull String path() {
        return path;
    }

    public boolean abstractValue() {
        return abstractValue;
    }

    public FindableResource copy() {
        FindableResource model = new FindableResource(typeName, messageClass, path, abstractValue);

        handleChildCopy(model);
        return model;
    }

    public static @NotNull FindableResource newAbstract(@NotNull String path) {
        Objects.requireNonNull(path, "path");

        return new FindableResource(null, null, path, true);
    }

    public static @NotNull FindableResource withType(@NotNull String path, @NotNull Class<?> valueType) {
        Objects.requireNonNull(path, "path");
        Objects.requireNonNull(valueType, "valueType");

        return new FindableResource(null, valueType, path, false);
    }

    public static @NotNull FindableResource withTypeName(@NotNull String path, @NotNull String valueTypeName) {
        Objects.requireNonNull(path, "path");
        Objects.requireNonNull(valueTypeName, "valueTypeName");

        return new FindableResource(valueTypeName, null, path, false);
    }
}