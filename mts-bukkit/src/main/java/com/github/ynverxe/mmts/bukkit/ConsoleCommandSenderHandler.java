package com.github.ynverxe.mmts.bukkit;

import com.github.ynverxe.mmts.core.MMTSHandler;
import com.github.ynverxe.mmts.core.entity.AllInOneEntityHandler;
import org.bukkit.command.ConsoleCommandSender;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Locale;
import java.util.Objects;

public class ConsoleCommandSenderHandler extends AllInOneEntityHandler<ConsoleCommandSender> {
    @Override
    public @NotNull Class<ConsoleCommandSender> type() {
        return ConsoleCommandSender.class;
    }

    @Override
    public void onNoMessageFound(@NotNull MMTSHandler mmtsHandler, @NotNull ConsoleCommandSender entity, @NotNull String missingPath) {
        entity.sendMessage(missingPath);
    }

    @Override
    public void sendMessage(@NotNull ConsoleCommandSender entity, @NotNull String mode, @NotNull Object message) {
        entity.sendMessage(Objects.toString(message));
    }

    @Override
    public boolean offer(@NotNull Class<?> messageClass) {
        return true;
    }

    @Override
    public @Nullable String resolveLanguage(ConsoleCommandSender entity) {
        return Locale.getDefault().getLanguage();
    }
}