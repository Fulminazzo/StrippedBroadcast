package it.fulminazzo.sbc.Listeners;

import it.fulminazzo.sbc.StrippedBroadcast;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

public class PlayerListener implements Listener {
    private final StrippedBroadcast plugin;

    public PlayerListener(StrippedBroadcast plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerChat(AsyncPlayerChatEvent event) {
        Player player = event.getPlayer();
        if (plugin.isPlayerBroadcasting(player)) {
            event.setCancelled(true);
            String message = plugin.isRainbowBroadcast(player) && !event.getMessage().toLowerCase().startsWith("[rainbow]") ?
                    "[RAINBOW] " + event.getMessage() : event.getMessage();
            Bukkit.getScheduler().runTask(plugin, () ->
                    StrippedBroadcast.sendStrippedBroadcast(plugin.getPlayerBroadcastingPlayers(player), message));
        }
    }
}