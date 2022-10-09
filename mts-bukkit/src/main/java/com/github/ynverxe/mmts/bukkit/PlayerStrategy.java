package com.github.ynverxe.mmts.bukkit;

import com.github.ynverxe.mmts.core.entity.EntityStrategy;
import com.github.ynverxe.mmts.core.message.Messenger;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class PlayerStrategy implements EntityStrategy<Player> {
    @Override
    public void onNoMessageFound(@NotNull Messenger messenger, @NotNull Player entity, @NotNull String path) {
        entity.sendMessage(path);
    }
}