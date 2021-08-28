package it.fulminazzo.sbc;

import it.fulminazzo.hexcolorsutil.HexColorsUtil;
import it.fulminazzo.sbc.Commands.StrippedBroadcastCommand;
import it.fulminazzo.sbcAPI.StrippedBroadcastEvent;
import it.fulminazzo.sbc.Utils.StringsUtil;
import net.luckperms.api.LuckPerms;
import net.md_5.bungee.api.ChatColor;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

public class StrippedBroadcast extends JavaPlugin {
    private static HexColorsUtil hexColorsUtil;
    private static StringsUtil stringsUtil;
    private boolean server1_8;
    private LuckPerms luckPerms;
    private Economy economy;

    /**
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
        stringsUtil = new StringsUtil();
        getCommand("sbc").setExecutor(new StrippedBroadcastCommand(this));
        getCommand("sbc").setTabCompleter(new StrippedBroadcastCommand(this));
    }

    /**
     * Sends a formatted broadcast to the given players collection.
     *
     * @param players: the targets.
     * @param strings: the array containing the message.
     */
    public static void sendStrippedBroadcast(Collection<? extends Player> players, String[] strings) {
        sendStrippedBroadcast(new ArrayList<>(players), strings);
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
     * @param message: the string containing the message.
     */
    public static void sendStrippedBroadcast(Collection<? extends Player> players, String message) {
        sendStrippedBroadcast(new ArrayList<>(players), message);
    }

    /**
     * Sends a formatted broadcast to the given players list.
     *
     * @param players: the targets.
     * @param strings: the array containing the message.
     */
    public static void sendStrippedBroadcast(List<Player> players, String[] strings) {
        sendStrippedBroadcast(players, Arrays.asList(strings));
    }

    /**
     * Sends a formatted broadcast to the given players list.
     *
     * @param players: the targets.
     * @param list: the list containing the message.
     */
    public static void sendStrippedBroadcast(List<Player> players, List<String> list) {
        sendStrippedBroadcast(players, stringsUtil.getParsedMessage(list, true));
    }

    /**
     * Sends a formatted broadcast to the given players list.
     *
     * @param players: the targets.
     * @param message: the string containing the message.
     */
    public static void sendStrippedBroadcast(List<Player> players, String message) {
        message = stringsUtil.parseString(message.replace("  ", " "));
        if (message.toUpperCase().startsWith("[RAINBOW] ")) message = hexColorsUtil.parseRainbowEffect(message.substring(10));
        else message = hexColorsUtil.translateHexColorCodes(message);

        if (ChatColor.stripColor(message).replace(" ", "").equalsIgnoreCase("")) return;
        Bukkit.getConsoleSender().sendMessage(message);
        for (Player p : players) p.sendMessage(message);

        StrippedBroadcastEvent event = new StrippedBroadcastEvent(players, message);
        Bukkit.getServer().getPluginManager().callEvent(event);
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
    public StringsUtil getStringsUtil() {
        return stringsUtil;
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