package com.github.ynverxe.mmts.bukkit;

import com.github.ynverxe.mmts.translation.AbstractFileSourceCreator;
import com.github.ynverxe.mmts.translation.TranslationSource;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

import java.io.*;
import java.util.Objects;

public final class YamlSourceCreator extends AbstractFileSourceCreator {

    private final Plugin plugin;

    public YamlSourceCreator(File folder, String extension, Plugin plugin) {
        super(folder, extension);
        this.plugin = Objects.requireNonNull(plugin, "plugin");
    }

    @Override
    protected @NotNull TranslationSource consumeExistentFile(@NotNull File file, boolean recentlyCreated) {
        try {
            String fileName = file.getName();
            YamlConfiguration yamlConfiguration = null;

            if (recentlyCreated) {
                InputStream inputStream = plugin.getResource(fileName);

                if (inputStream != null) {
                    yamlConfiguration = YamlConfiguration.loadConfiguration(inputStream);

                    yamlConfiguration.save(file);
                }
            }

            if (yamlConfiguration == null) {
                (yamlConfiguration = new YamlConfiguration()).load(file);
            }

            return new YamlTranslationSource(yamlConfiguration);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}