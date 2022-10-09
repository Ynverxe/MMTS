package com.github.ynverxe.mmts.bukkit;

import com.github.ynverxe.mmts.core.AbstractMMTSModule;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

public class BukkitMMTSModule extends AbstractMMTSModule {
    @Override
    public void configure() {
        getEntityContainer(Player.class, false)
                .bindLinguist(new SpigotLinguist())
                .bindStrategy(new PlayerStrategy());

        getEntityContainer(ConsoleCommandSender.class, false)
                .bindSender(new ConsoleCommandSenderMessageSender())
                .bindStrategy(new ConsoleCommandSenderStrategy())
                .bindLinguist(new ConsoleCommandSenderLinguist());
    }
}