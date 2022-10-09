package com.github.ynverxe.mmts.translation;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.IOException;
import java.util.Objects;

public abstract class AbstractFileSourceCreator implements SourceCreator {

    private final File folder;
    private final String fileFormat;

    public AbstractFileSourceCreator(File folder, String fileFormat) {
        if (!folder.exists() && !folder.mkdirs()) {
            throw new RuntimeException("Unable to create folder");
        }

        this.folder = folder;
        this.fileFormat = Objects.requireNonNull(fileFormat);
    }

    @Override
    public @Nullable TranslationSource createSource(@NotNull String lang) {
        File file = new File(folder, fileFormat.replace("<lang>", lang));
        boolean recentlyCreated = false;

        try {
            if (!file.exists() && !(recentlyCreated = file.createNewFile())) {
                throw createException(lang, null);
            }
        } catch (IOException e) {
            throw createException(lang, e);
        }

        return consumeExistentFile(file, recentlyCreated);
    }

    private RuntimeException createException(String lang, Throwable cause) {
        String message = "Unable to create file for: " + lang;

        if (cause == null) {
            return new RuntimeException(message);
        }

        return new RuntimeException(message, cause);
    }

    protected abstract @Nullable TranslationSource consumeExistentFile(@NotNull File file, boolean recentlyCreated);
}