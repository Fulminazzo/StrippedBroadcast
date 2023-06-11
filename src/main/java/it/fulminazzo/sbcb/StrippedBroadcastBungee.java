package it.fulminazzo.sbcb;

import it.fulminazzo.Utils.StringsUtil;
import it.fulminazzo.sbcAPI.StrippedBroadcastEventB;
import it.fulminazzo.sbcb.Commands.StrippedBroadcastBCommand;
import it.fulminazzo.sbcb.Listeners.PlayerListener;
import it.fulminazzo.sbcb.Utils.MessagesUtilB;
import net.luckperms.api.LuckPerms;
import net.luckperms.api.LuckPermsProvider;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.api.plugin.PluginManager;

import java.util.*;
import java.util.stream.Collectors;

public class StrippedBroadcastBungee extends Plugin {
    private static MessagesUtilB messagesUtilB;
    private static LuckPerms luckPerms;
    private HashMap<UUID, List<UUID>> broadcastingPlayers;

    @Override
    public void onEnable() {
        if (isPluginEnabled("LuckPerms")) luckPerms = LuckPermsProvider.get();
        messagesUtilB = new MessagesUtilB();
        broadcastingPlayers = new HashMap<>();
        PluginManager pluginManager = getProxy().getPluginManager();
        pluginManager.registerListener(this, new PlayerListener(this));
        pluginManager.registerCommand(this, new StrippedBroadcastBCommand(this));
    }

    /**
     * Sends a formatted broadcast to the given players collection.
     *
     * @param players: the targets.
     * @param strings: the array containing the message.
     */
    public static void sendStrippedBroadcast(Collection<ProxiedPlayer> players, String[] strings) {
        sendStrippedBroadcast(new ArrayList<>(players), strings);
    }

    /**
     * Sends a formatted broadcast to the given players collection.
     *
     * @param players: the targets.
     * @param list: the list containing the message.
     */
    public static void sendStrippedBroadcast(Collection<ProxiedPlayer> players, List<String> list) {
        sendStrippedBroadcast(new ArrayList<>(players), list);
    }

    /**
     * Sends a formatted broadcast to the given players collection.
     *
     * @param players: the targets.
     * @param message: the string containing the message.
     */
    public static void sendStrippedBroadcast(Collection<ProxiedPlayer> players, String message) {
        sendStrippedBroadcast(new ArrayList<>(players), message);
    }

    /**
     * Sends a formatted broadcast to the given players list.
     *
     * @param players: the targets.
     * @param strings: the array containing the message.
     */
    public static void sendStrippedBroadcast(List<ProxiedPlayer> players, String[] strings) {
        sendStrippedBroadcast(players, Arrays.asList(strings));
    }

    /**
     * Sends a formatted broadcast to the given players list.
     *
     * @param players: the targets.
     * @param list: the list containing the message.
     */
    public static void sendStrippedBroadcast(List<ProxiedPlayer> players, List<String> list) {
        sendStrippedBroadcast(players, StringsUtil.getParsedMessage(list, true));
    }

    /**
     * Sends a formatted broadcast to the given players list.
     *
     * @param players: the targets.
     * @param message: the string containing the message.
     */
    public static void sendStrippedBroadcast(List<ProxiedPlayer> players, String message) {
        if (message.contains("\n")) {
            List<ProxiedPlayer> finalPlayers = players;
            Arrays.stream(message.replace("\n", "\n ").split("\n")).forEach(m -> sendStrippedBroadcast(finalPlayers, m));
            return;
        }
        message = StringsUtil.parseString(message.replace("  ", " "));

        if (ChatColor.stripColor(message).replace(" ", "").equalsIgnoreCase("")) return;

        StrippedBroadcastEventB event = new StrippedBroadcastEventB(players, message);
        ProxyServer.getInstance().getPluginManager().callEvent(event);
        if (event.isCancelled()) return;
        message = event.getMessage();
        players = event.getPlayers();

        TextComponent finalMessage = messagesUtilB.parseTextComponent(message);
        ProxyServer.getInstance().getConsole().sendMessage(finalMessage);
        for (ProxiedPlayer p : players) p.sendMessage(finalMessage);
    }

    /**
     * Checks if a player is in the broadcastingPlayer list.
     *
     * @param player: the player.
     * @return true or false.
     */
    public boolean isPlayerBroadcasting(ProxiedPlayer player) {
        return isPlayerBroadcasting(player.getUniqueId());
    }

    /**
     * Checks if a player is in the broadcastingPlayer list.
     *
     * @param uuid: the player uuid.
     * @return true or false.
     */
    public boolean isPlayerBroadcasting(UUID uuid) {
        return broadcastingPlayers.containsKey(uuid);
    }

    /**
     * Changes the player broadcasting. If he is, then
     * it will be removed, else it will be added.
     *
     * @param player: the player.
     * @param players: the players to broadcast to.
     */
    public void flipPlayerBroadcasting(ProxiedPlayer player, List<ProxiedPlayer> players) {
        flipPlayerBroadcasting(player.getUniqueId(), players.stream().map(ProxiedPlayer::getUniqueId).collect(Collectors.toList()));
    }

    /**
     * Changes the player broadcasting. If he is, then
     * it will be removed, else it will be added.
     *
     * @param uuid: the player uuid.
     * @param uuids: the players to broadcast to.
     */
    public void flipPlayerBroadcasting(UUID uuid, List<UUID> uuids) {
        if (isPlayerBroadcasting(uuid)) broadcastingPlayers.remove(uuid);
        else broadcastingPlayers.put(uuid, uuids);
    }

    /**
     * Returns a list of players that the player is broadcasting to.
     * Requires to have done /sbc <players>
     *
     * @param player: the player who is broadcasting.
     *
     * @return players: the players to broadcast to.
     */
    public List<ProxiedPlayer> getPlayerBroadcastingPlayers(ProxiedPlayer player) {
        return getPlayerBroadcastingPlayers(player.getUniqueId());
    }

    /**
     * Returns a list of players that the player is broadcasting to.
     * Requires to have done /sbc <players>
     *
     * @param uuid: the player uuid who is broadcasting.
     *
     * @return players: the players to broadcast to.
     */
    public List<ProxiedPlayer> getPlayerBroadcastingPlayers(UUID uuid) {
        if (!isPlayerBroadcasting(uuid)) return new ArrayList<>();
        return broadcastingPlayers.get(uuid).stream()
                .filter(u -> getProxy().getPlayer(u) != null)
                .map(u -> getProxy().getPlayer(u))
                .collect(Collectors.toList());
    }

    /**
     * Checks if LuckPerms was detected by the plugin.
     *
     * @return if LuckPerms is enabled or not.
     */
    public static boolean isLuckPermsEnabled() {
        return luckPerms != null;
    }

    /**
     * Gets an instance of LuckPerms.
     *
     * @return the instance.
     */
    public LuckPerms getLuckPerms() {
        return luckPerms;
    }

    /**
     * Gets an instance of StringUtil.
     *
     * @return the instance.
     */
    public MessagesUtilB getStringsUtilB() {
        return messagesUtilB;
    }

    /**
     * Checks if a plugin is present in the server.
     *
     * @return if the plugin is present or not.
     */
    public boolean isPluginEnabled(String pluginName) {
        return getProxy().getPluginManager().getPlugin(pluginName) != null;
    }
}