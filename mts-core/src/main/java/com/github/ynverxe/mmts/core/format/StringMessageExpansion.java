package com.github.ynverxe.mmts.core.format;

import com.github.ynverxe.data.DataNode;
import com.github.ynverxe.mmts.translation.TranslationData;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

public class StringMessageExpansion implements MessageExpansion<String> {

    @Override
    public @NotNull String createNewMessage(
            @NotNull TranslationData translationData,
            @NotNull MessageFormatter messageFormatter,
            @NotNull FormattingContext formattingContext
    ) {
        String path = translationData.getPath();
        path = path != null ? path : "non-existent";

        String rawMessage = translationData
                .firstValueOrEmpty()
                .map(Objects::toString)
                .orElse(path);

        return messageFormatter.formatString(rawMessage, FormattingContext.consuming(formattingContext));
    }

    @Override
    public @Nullable TranslationData dismountAsData(@NotNull String obj) {
        DataNode dataNode = new DataNode();
        dataNode.put("value", obj);
        return TranslationData.withoutPath(dataNode);
    }
}