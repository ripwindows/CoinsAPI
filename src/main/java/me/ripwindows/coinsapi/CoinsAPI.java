package me.ripwindows.coinsapi;

import lombok.Getter;
import me.ripwindows.coinsapi.api.database.Database;
import me.ripwindows.coinsapi.api.database.DatabaseFactory;
import me.ripwindows.coinsapi.listeners.JoinAndQuitListener;
import me.ripwindows.coinsapi.utils.SimpleConfig;
import org.bukkit.plugin.java.JavaPlugin;
import org.checkerframework.checker.nullness.qual.MonotonicNonNull;


public class CoinsAPI extends JavaPlugin {
    private @MonotonicNonNull @Getter static CoinsAPI instance;
    private @Getter Database database;

    private @Getter SimpleConfig settingsConfig;

    @Override
    public void onEnable() {
        instance = this;

        settingsConfig =
                new SimpleConfig("settings.yml", CoinsAPI.getInstance().getDataFolder().getPath());

        String databaseType = settingsConfig.getConfig().getString("database-type");

        database = DatabaseFactory.getFactory().createDatabase(databaseType);
        database.initialize();

        getServer().getPluginManager().registerEvents(new JoinAndQuitListener(), this);
    }

    @Override
    public void onDisable() {
        database.disconnect();
    }
}