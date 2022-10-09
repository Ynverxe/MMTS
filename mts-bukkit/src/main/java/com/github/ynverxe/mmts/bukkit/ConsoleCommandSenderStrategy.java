package com.github.ynverxe.mmts.bukkit;

import com.github.ynverxe.mmts.core.entity.EntityStrategy;
import com.github.ynverxe.mmts.core.message.Messenger;
import org.bukkit.command.ConsoleCommandSender;
import org.jetbrains.annotations.NotNull;

public class ConsoleCommandSenderStrategy implements EntityStrategy<ConsoleCommandSender> {
    @Override
    public void onNoMessageFound(@NotNull Messenger messenger, @NotNull ConsoleCommandSender entity, @NotNull String path) {
        entity.sendMessage(path);
    }
}