package me.ripwindows.coinsapi.utils;

import lombok.Getter;
import me.ripwindows.coinsapi.CoinsAPI;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;


public class SimpleConfig {

    public String path;

    @Getter
    private final File file;

    private YamlConfiguration cfg;

    public SimpleConfig(String name, String path) {
        this.path = path;
        (new File(path)).mkdir();
        file = new File(path, name);
        if (!file.exists())
            try {
                CoinsAPI.getInstance().saveResource(name, true);
                cfg = YamlConfiguration.loadConfiguration(file);
            } catch (Exception ignored) {
            }
        cfg = YamlConfiguration.loadConfiguration(file);
    }


    public boolean exists() {
        return file.exists();
    }

    public void delete() {
        file.delete();
        cfg = null;
    }

    public YamlConfiguration getConfig() {
        if (cfg == null)
            cfg = YamlConfiguration.loadConfiguration(file);
        return cfg;
    }

    public void save() {
        try {
            cfg.save(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
