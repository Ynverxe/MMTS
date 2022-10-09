package com.github.ynverxe.mmts.bukkit;

import com.github.ynverxe.mmts.translation.TranslationSource;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

public class YamlTranslationSource implements TranslationSource {

    private final YamlConfiguration yamlConfiguration;

    public YamlTranslationSource(YamlConfiguration yamlConfiguration) {
        this.yamlConfiguration = Objects.requireNonNull(yamlConfiguration, "yamlConfiguration");
    }

    @Override
    public @Nullable Object findData(@Nullable String path) {
        Object found = yamlConfiguration.get(path);

        if (found instanceof ConfigurationSection) {
            found = ((ConfigurationSection) found).getValues(true);
        } else if (found instanceof ConfigurationSerializable) {
            found = ((ConfigurationSerializable) found).serialize();
        }

        return found;
    }
}