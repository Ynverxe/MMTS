package com.github.ynverxe.mmts.bukkit;

import com.github.ynverxe.mmts.core.MMTSHandler;
import com.github.ynverxe.mmts.core.entity.AllInOneEntityHandler;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

public class PlayerHandler extends AllInOneEntityHandler<Player> {
    @Override
    public @NotNull Class<Player> type() {
        return Player.class;
    }

    @Override
    public void onNoMessageFound(@NotNull MMTSHandler mmtsHandler, @NotNull Player entity, @NotNull String missingPath) {
        entity.sendMessage(missingPath);
    }

    @Override
    public void sendMessage(@NotNull Player entity, @NotNull String mode, @NotNull Object message) {
        entity.sendMessage(Objects.toString(message));
    }

    @Override
    public boolean offer(@NotNull Class<?> messageClass) {
        return true;
    }

    @Override
    public @Nullable String resolveLanguage(Player entity) {
        return entity.spigot().getLocale().split("_")[0];
    }
}