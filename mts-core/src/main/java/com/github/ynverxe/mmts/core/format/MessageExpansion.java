package com.github.ynverxe.mmts.core.format;

import com.github.ynverxe.mmts.translation.MessageData;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface MessageExpansion<T> {

    @NotNull T createNewMessage(
            @NotNull MessageData messageData,
            @NotNull MessageFormatter messageFormatter,
            @NotNull FormattingContext formattingContext
    );

    @Nullable MessageData dismountAsData(@NotNull T obj);

}