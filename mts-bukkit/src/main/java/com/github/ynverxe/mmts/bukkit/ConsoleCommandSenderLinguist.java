package com.github.ynverxe.mmts.bukkit;

import com.github.ynverxe.mmts.translation.Linguist;
import org.bukkit.command.ConsoleCommandSender;
import org.jetbrains.annotations.Nullable;

import java.util.Locale;

public class ConsoleCommandSenderLinguist implements Linguist<ConsoleCommandSender> {
    @Override
    public @Nullable String resolveLanguage(ConsoleCommandSender entity) {
        return Locale.getDefault().getLanguage();
    }
}