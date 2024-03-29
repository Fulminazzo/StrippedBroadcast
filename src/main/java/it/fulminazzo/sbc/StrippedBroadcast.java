package it.fulminazzo.sbc;

import it.angrybear.Utils.ActionbarUtils;
import it.angrybear.Utils.TitleUtils;
import it.fulminazzo.Utils.StringsUtil;
import it.fulminazzo.hexcolorsutil.HexColorsUtil;
import it.fulminazzo.sbc.Commands.StrippedBroadcastCommand;
import it.fulminazzo.sbc.Enums.MessageType;
import it.fulminazzo.sbc.Listeners.PlayerListener;
import it.fulminazzo.sbc.Objects.BroadcastingPlayer;
import it.fulminazzo.sbc.Utils.MessagesUtil;
import it.fulminazzo.sbcAPI.StrippedBroadcastEvent;
import net.luckperms.api.LuckPerms;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.*;
import java.util.stream.Collectors;

public class StrippedBroadcast extends JavaPlugin {
    private static HexColorsUtil hexColorsUtil;
    private static MessagesUtil messagesUtil;
    private boolean server1_8;
    private LuckPerms luckPerms;
    private Economy economy;
    private List<BroadcastingPlayer> broadcastingPlayers;

    /*
     * The main method of the plugin.
     * In here we create a new instance for HexColorsUtil and StringUtils,
     * and we get the commands.
     */
    @Override
    public void onEnable() {
        String version = Bukkit.getBukkitVersion();
        server1_8 = (!version.contains("1.1") || version.contains("1.1.")) && !version.contains("1.9");
        if (isPluginEnabled("LuckPerms")) {
            RegisteredServiceProvider<LuckPerms> luckPermsProvider = Bukkit.getServicesManager().getRegistration(LuckPerms.class);
            if (luckPermsProvider != null) luckPerms = luckPermsProvider.getProvider();
        }
        if (isPluginEnabled("Vault")) {
            RegisteredServiceProvider<Economy> vaultProvider = Bukkit.getServicesManager().getRegistration(Economy.class);
            if (vaultProvider != null) economy = vaultProvider.getProvider();
        }
        hexColorsUtil = new HexColorsUtil();
        messagesUtil = new MessagesUtil();
        broadcastingPlayers = new ArrayList<>();
        saveDefaultConfig();
        Bukkit.getPluginManager().registerEvents(new PlayerListener(this), this);
        getCommand("sbc").setExecutor(new StrippedBroadcastCommand(this));

        ConfigurationSection messagesSection = getConfig().getConfigurationSection("messages");
        if (messagesSection == null) return;
        messagesSection.getStringList("startup").forEach(c ->
                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), c.startsWith("/") ? c.substring(1) : c)
        );
    }

    /**
     * Sends a formatted broadcast to the given players collection.
     *
     * @param players: the targets.
     * @param strings: the array containing the message.
     */
    public static void sendStrippedBroadcast(Collection<? extends Player> players, String... strings) {
        sendStrippedBroadcast(new ArrayList<>(players), strings);
    }

    /**
     * Sends a formatted broadcast to the given players collection.
     *
     * @param players: the targets.
     * @param messageType: the type of the message.
     * @param strings: the array containing the message.
     */
    public static void sendStrippedBroadcast(Collection<? extends Player> players, MessageType messageType, String... strings) {
        sendStrippedBroadcast(new ArrayList<>(players), messageType, strings);
    }

    /**
     * Sends a formatted broadcast to the given players collection.
     *
     * @param players: the targets.
     * @param list: the list containing the message.
     */
    public static void sendStrippedBroadcast(Collection<? extends Player> players, List<String> list) {
        sendStrippedBroadcast(new ArrayList<>(players), list);
    }

    /**
     * Sends a formatted broadcast to the given players collection.
     *
     * @param players: the targets.
     * @param messageType: the type of the message.
     * @param list: the list containing the message.
     */
    public static void sendStrippedBroadcast(Collection<? extends Player> players, MessageType messageType, List<String> list) {
        sendStrippedBroadcast(new ArrayList<>(players), messageType, list);
    }

    /**
     * Sends a formatted broadcast to the given players collection.
     *
     * @param players: the targets.
     * @param message: the string containing the message.
     */
    public static void sendStrippedBroadcast(Collection<? extends Player> players, String message) {
        sendStrippedBroadcast(new ArrayList<>(players), message);
    }

    /**
     * Sends a formatted broadcast to the given players collection.
     *
     * @param players: the targets.
     * @param messageType: the type of the message.
     * @param message: the string containing the message.
     */
    public static void sendStrippedBroadcast(Collection<? extends Player> players, MessageType messageType, String message) {
        sendStrippedBroadcast(new ArrayList<>(players), messageType, message);
    }

    /**
     * Sends a formatted broadcast to the given players list.
     *
     * @param players: the targets.
     * @param strings: the array containing the message.
     */
    public static void sendStrippedBroadcast(List<Player> players, String... strings) {
        sendStrippedBroadcast(players, Arrays.asList(strings));
    }

    /**
     * Sends a formatted broadcast to the given players list.
     *
     * @param players: the targets.
     * @param messageType: the type of the message.
     * @param strings: the array containing the message.
     */
    public static void sendStrippedBroadcast(List<Player> players, MessageType messageType, String... strings) {
        sendStrippedBroadcast(players, messageType, Arrays.asList(strings));
    }

    /**
     * Sends a formatted broadcast to the given players list.
     *
     * @param players: the targets.
     * @param list: the list containing the message.
     */
    public static void sendStrippedBroadcast(List<Player> players, List<String> list) {
        sendStrippedBroadcast(players, StringsUtil.getParsedMessage(list, true));
    }

    /**
     * Sends a formatted broadcast to the given players list.
     *
     * @param players: the targets.
     * @param messageType: the type of the message.
     * @param list: the list containing the message.
     */
    public static void sendStrippedBroadcast(List<Player> players, MessageType messageType, List<String> list) {
        sendStrippedBroadcast(players, messageType, StringsUtil.getParsedMessage(list, true));
    }

    /**
     * Sends a formatted broadcast to the given players list.
     *
     * @param players: the targets.
     * @param message: the string containing the message.
     */
    public static void sendStrippedBroadcast(List<Player> players, String message) {
        sendStrippedBroadcast(players, MessageType.MESSAGE, message);
    }

    /**
     * Sends a formatted broadcast to the given players list.
     *
     * @param players: the targets.
     * @param messageType: the type of the message.
     * @param message: the string containing the message.
     */
    public static void sendStrippedBroadcast(List<Player> players, MessageType messageType, String message) {
        if (message.contains("\n")) {
            List<Player> finalPlayers = players;
            Arrays.stream(message.replace("\n", "\n ").split("\n")).forEach(m -> sendStrippedBroadcast(finalPlayers, m));
            return;
        }
        message = parseMessage(message);

        StrippedBroadcastEvent event = new StrippedBroadcastEvent(players, messageType, message);
        Bukkit.getServer().getPluginManager().callEvent(event);
        if (event.isCancelled()) return;
        message = event.getMessage();
        messageType = event.getMessageType();
        players = event.getPlayers();
        if (message == null) message = "";

        Bukkit.getConsoleSender().sendMessage(message);
        for (Player p : players)
            switch (messageType) {
                case TITLE: TitleUtils.sendTitle(p, message); break;
                case SUBTITLE: TitleUtils.sendSubTitle(p, message); break;
                case ACTIONBAR: ActionbarUtils.sendActionBar(p, message); break;
                default: p.sendMessage(message);
            }
    }

    public static String parseMessage(String message) {
        message = StringsUtil.parseString(message.replace("  ", " "));
        if (message.toUpperCase().startsWith("[RAINBOW]"))
            message = hexColorsUtil.parseRainbowEffect(message.substring("[RAINBOW]".length()).trim());
        else message = hexColorsUtil.translateHexColorCodes(message);
        return message.trim();
    }

    /*
     * Returns the corresponding broadcasting player if he/she is broadcasting.
     *
     * @param player: the player.
     */
    public BroadcastingPlayer getBroadcastingPlayer(Player player) {
        return getBroadcastingPlayer(player == null ? null : player.getUniqueId());
    }

    /*
     * Returns the corresponding broadcasting player if he/she is broadcasting.
     *
     * @param uuid: the player's uuid.
     */
    public BroadcastingPlayer getBroadcastingPlayer(UUID uuid) {
        return this.broadcastingPlayers.stream().filter(p -> p.getUuid().equals(uuid)).findAny().orElse(null);
    }

    /**
     * Checks if a player is in the broadcastingPlayer list.
     *
     * @param player: the player.
     * @return true or false.
     */
    public boolean isPlayerBroadcasting(Player player) {
        return isPlayerBroadcasting(player.getUniqueId());
    }

    /**
     * Checks if a player is in the broadcastingPlayer list.
     *
     * @param uuid: the player uuid.
     * @return true or false.
     */
    public boolean isPlayerBroadcasting(UUID uuid) {
        return getBroadcastingPlayer(uuid) != null;
    }

    /**
     * Changes the player broadcasting. If he is, then
     * it will be removed, else it will be added.
     *
     * @param player: the player.
     * @param players: the players to broadcast to.
     * @param messageType: the type of the message.
     * @param rainbow: if the broadcast is rainbow.
     */
    public void flipPlayerBroadcasting(Player player, List<Player> players, MessageType messageType, boolean rainbow) {
        flipPlayerBroadcasting(player.getUniqueId(), players.stream().map(Player::getUniqueId).collect(Collectors.toList()), messageType, rainbow);
    }

    /**
     * Changes the player broadcasting. If he is, then
     * it will be removed, else it will be added.
     *
     * @param uuid: the player uuid.
     * @param uuids: the players to broadcast to.
     * @param messageType: the type of the message.
     * @param rainbow: if the broadcast is rainbow.
     */
    public void flipPlayerBroadcasting(UUID uuid, List<UUID> uuids, MessageType messageType, boolean rainbow) {
        if (isPlayerBroadcasting(uuid)) {
            broadcastingPlayers.removeIf(p -> p.getUuid().equals(uuid));
        }
        else broadcastingPlayers.add(new BroadcastingPlayer(uuid, uuids, messageType, rainbow));
    }

    /**
     * Returns a list of players that the player is broadcasting to.
     * Requires to have done /sbc <players>
     *
     * @param player: the player who is broadcasting.
     *
     * @return players: the players to broadcast to.
     */
    public List<Player> getPlayerBroadcastingPlayers(Player player) {
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
    public List<Player> getPlayerBroadcastingPlayers(UUID uuid) {
        if (!isPlayerBroadcasting(uuid)) return new ArrayList<>();
        return getBroadcastingPlayer(uuid).getReceivers().stream()
                .map(Bukkit::getPlayer)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    /**
     * Checks if Vault was detected by the plugin.
     *
     * @return if Vault is enabled or not.
     */
    public boolean isVaultEnabled() {
        return economy != null;
    }

    /**
     * Gets an instance of Vault Economy.
     *
     * @return the instance.
     */
    public Economy getEconomy() {
        return economy;
    }

    /**
     * Checks if LuckPerms was detected by the plugin.
     *
     * @return if LuckPerms is enabled or not.
     */
    public boolean isLuckPermsEnabled() {
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
     * Gets an instance of HexColorsUtil.
     *
     * @return the instance.
     */
    public HexColorsUtil getHexColorsUtil() {
        return hexColorsUtil;
    }

    /**
     * Gets an instance of StringUtil.
     *
     * @return the instance.
     */
    public MessagesUtil getStringsUtil() {
        return messagesUtil;
    }

    /**
     * Checks if a plugin is present in the server.
     *
     * @return if the plugin is present or not.
     */
    public boolean isPluginEnabled(String pluginName) {
        return Bukkit.getPluginManager().getPlugin(pluginName) != null;
    }

    /**
     * Checks if server is 1.8 or below.
     *
     * @return true if the server is in 1.8.
     */
    public boolean isServer1_8() {
        return server1_8;
    }
}