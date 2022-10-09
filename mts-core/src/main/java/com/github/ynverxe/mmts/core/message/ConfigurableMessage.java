package com.github.ynverxe.mmts.core.message;

import com.github.ynverxe.mmts.core.format.FormattingContext;
import com.github.ynverxe.mmts.core.placeholder.PlaceholderDelimiterPack;
import org.jetbrains.annotations.NotNull;

public interface ConfigurableMessage {

    @NotNull ConfigurableMessage configureContext(@NotNull FormattingContext.Configurator contextConfigurator);

    @NotNull ConfigurableMessage replacing(@NotNull Object... replacements);

    @NotNull ConfigurableMessage placeholderDelimiterPack(@NotNull PlaceholderDelimiterPack placeholderDelimiterPack);

    @NotNull ConfigurableMessage placeholderDelimiter(char delimiter);

    @NotNull ConfigurableMessage skipPlaceholderFormatting(boolean b);

    void send(@NotNull Object entityOrEntities);

    void send(@NotNull Object entityOrEntities, @NotNull String mode);

}