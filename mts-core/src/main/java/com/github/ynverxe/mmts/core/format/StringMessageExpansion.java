package com.github.ynverxe.mmts.core.format;

import com.github.ynverxe.data.DataNode;
import com.github.ynverxe.mmts.translation.MessageData;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

public class StringMessageExpansion implements MessageExpansion<String> {

    @Override
    public @NotNull String createNewMessage(
            @NotNull MessageData messageData,
            @NotNull MessageFormatter messageFormatter,
            @NotNull FormattingContext formattingContext
    ) {
        String path = messageData.getPath();
        path = path != null ? path : "non-existent";

        String rawMessage = messageData
                .firstValueOrEmpty()
                .map(Objects::toString)
                .orElse(path);

        return messageFormatter.formatString(rawMessage, FormattingContext.consuming(formattingContext));
    }

    @Override
    public @Nullable MessageData dismountAsData(@NotNull String obj) {
        DataNode dataNode = new DataNode();
        dataNode.put("value", obj);
        return MessageData.withoutPath(dataNode);
    }
}