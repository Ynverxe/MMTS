package com.github.ynverxe.mmts.bukkit;

import com.github.ynverxe.mmts.core.entity.MessageSender;
import org.bukkit.command.ConsoleCommandSender;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class ConsoleCommandSenderMessageSender implements MessageSender<ConsoleCommandSender> {
    @Override
    public void sendMessage(@NotNull ConsoleCommandSender entity, @NotNull String mode, @NotNull Object message) {
        entity.sendMessage(Objects.toString(message));
    }

    @Override
    public boolean offer(@NotNull Class<?> messageClass) {
        return true;
    }
}