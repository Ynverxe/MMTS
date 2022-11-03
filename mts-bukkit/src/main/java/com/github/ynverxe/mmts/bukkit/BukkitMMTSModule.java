package com.github.ynverxe.mmts.bukkit;

import com.github.ynverxe.mmts.core.AbstractMMTSModule;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

public class BukkitMMTSModule extends AbstractMMTSModule {
    @Override
    public void configure() {
        new PlayerHandler().register(this);
        new ConsoleCommandSenderHandler().register(this);
    }
}