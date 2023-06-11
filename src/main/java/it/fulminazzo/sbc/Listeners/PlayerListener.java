package it.fulminazzo.sbc.Listeners;

import it.angrybear.Utils.ActionbarUtils;
import it.angrybear.Utils.TitleUtils;
import it.fulminazzo.sbc.Objects.BroadcastingPlayer;
import it.fulminazzo.sbc.StrippedBroadcast;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.List;
import java.util.Objects;

public class PlayerListener implements Listener {
    private final StrippedBroadcast plugin;

    public PlayerListener(StrippedBroadcast plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        parseTitles(plugin.getConfig().getConfigurationSection("titles.join"), player);
        parseActionBars("join", player);
        if (!player.hasPlayedBefore()) {
            parseTitles(plugin.getConfig().getConfigurationSection("titles.first-join"), player);
            parseActionBars("first-join", player);
        }
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        parseTitles(plugin.getConfig().getConfigurationSection("titles.quit"), player);
        parseActionBars("quit", player);
    }

    @EventHandler
    public void onPlayerChat(AsyncPlayerChatEvent event) {
        Player player = event.getPlayer();
        List<Player> players = plugin.getPlayerBroadcastingPlayers(player);
        if (plugin.isPlayerBroadcasting(player)) {
            BroadcastingPlayer broadcastingPlayer = plugin.getBroadcastingPlayer(player);
            String message = (broadcastingPlayer.isRainbow() && !event.getMessage().toLowerCase().startsWith("[rainbow]") ?
                        "[RAINBOW]" + event.getMessage() : event.getMessage());
            event.setCancelled(true);
            Bukkit.getScheduler().runTask(plugin, () -> StrippedBroadcast.sendStrippedBroadcast(players, broadcastingPlayer.getMessageType(), message));
        }
    }

    private void parseTitles(ConfigurationSection section, Player player) {
        if (section == null) return;
        section.getKeys(false).stream()
                .map(section::getConfigurationSection)
                .filter(Objects::nonNull)
                .map(s -> new String[]{s.getString("title"), s.getString("subtitle")})
                .filter(s -> s[0] != null)
                .peek(s -> s[1] = s[1] == null ? "" : s[1])
                .peek(s -> s[0] = ChatColor.translateAlternateColorCodes('&', s[0].replace("%player%", player.getName())))
                .peek(s -> s[1] = ChatColor.translateAlternateColorCodes('&', s[1].replace("%player%", player.getName())))
                .forEach(s -> TitleUtils.sendGeneralTitle(player, s[0], s[1], 20, 40, 20));
    }

    private void parseActionBars(String path, Player player) {
        String actionBarString = plugin.getConfig().getString("action-bars." + path);
        if (actionBarString == null) return;
        actionBarString = ChatColor.translateAlternateColorCodes('&', actionBarString);
        ActionbarUtils.sendActionBar(player, actionBarString);
    }
}