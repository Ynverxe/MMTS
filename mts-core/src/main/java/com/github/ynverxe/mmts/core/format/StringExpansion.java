package com.github.ynverxe.mmts.core.format;

import com.github.ynverxe.data.DataNode;
import com.github.ynverxe.mmts.translation.ResourceData;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

public class StringExpansion implements ObjectExpansion<String> {

    @Override
    public @NotNull String createNewMessage(
            @NotNull ResourceData resourceData,
            @NotNull ObjectFormatter objectFormatter,
            @NotNull FormattingMetricsHolder formattingMetricsHolder
    ) {
        String path = resourceData.getPath();
        path = path != null ? path : "non-existent";

        String rawMessage = resourceData
                .firstValueOrEmpty()
                .map(Objects::toString)
                .orElse(path);

        return objectFormatter.formatString(rawMessage, formattingMetricsHolder);
    }

    @Override
    public @Nullable ResourceData dismountAsData(@NotNull String obj) {
        DataNode dataNode = new DataNode();
        dataNode.put("value", obj);
        return ResourceData.withoutPath(dataNode);
    }
}