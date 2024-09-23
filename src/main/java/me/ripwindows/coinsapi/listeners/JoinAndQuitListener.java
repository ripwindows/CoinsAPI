package me.ripwindows.coinsapi.listeners;

import me.ripwindows.coinsapi.CoinsAPI;
import me.ripwindows.coinsapi.api.database.Database;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class JoinAndQuitListener implements Listener {
    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        Database database = CoinsAPI.getInstance().getDatabase();
        database.loadPlayer(player.getUniqueId());

//        database.getUser(player.getUniqueId())
//                .ifPresent(user -> player.sendMessage(user.getCoins() + ""));
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        Database database = CoinsAPI.getInstance().getDatabase();
        database.getUser(event.getPlayer().getUniqueId())
                .ifPresent(database::unloadAndSave);


    }
}
