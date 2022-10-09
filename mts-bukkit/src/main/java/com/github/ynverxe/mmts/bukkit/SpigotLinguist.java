package com.github.ynverxe.mmts.bukkit;

import com.github.ynverxe.mmts.translation.Linguist;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class SpigotLinguist implements Linguist<Player> {
    @Override
    public @Nullable String resolveLanguage(@NotNull Player entity) {
        return entity.spigot().getLocale().split("_")[0];
    }
}