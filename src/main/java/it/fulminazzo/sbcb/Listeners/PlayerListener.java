package it.fulminazzo.sbcb.Listeners;

import it.fulminazzo.sbcb.StrippedBroadcastBungee;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.ChatEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

public class PlayerListener implements Listener {
    private final StrippedBroadcastBungee plugin;

    public PlayerListener(StrippedBroadcastBungee plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerChat(ChatEvent event) {
        if (!(event.getSender() instanceof ProxiedPlayer)) return;
        ProxiedPlayer player = (ProxiedPlayer) event.getSender();
        if (plugin.isPlayerBroadcasting(player) && !event.getMessage().startsWith("/")) {
            event.setCancelled(true);
            StrippedBroadcastBungee.sendStrippedBroadcast(plugin.getPlayerBroadcastingPlayers(player), event.getMessage());
        }
    }
}