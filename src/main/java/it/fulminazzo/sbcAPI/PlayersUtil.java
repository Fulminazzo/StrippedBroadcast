package it.fulminazzo.sbcAPI;

import it.fulminazzo.Utils.NumberUtil;
import it.fulminazzo.sbc.StrippedBroadcast;
import it.fulminazzo.sbc.Utils.MessagesUtil;
import net.milkbowl.vault.economy.Economy;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.permissions.Permission;
import org.bukkit.permissions.ServerOperator;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.*;
import java.util.stream.Collectors;

public class PlayersUtil {
    /*
      This class will be used by the plugin, and
      it will be available for the API to get a
      list of players according to the given
      input.
     */

    /**
     * @return a list of all players in the server.
     */
    public static List<Player> getAllPlayers() {
        return new ArrayList<>(Bukkit.getOnlinePlayers());
    }

    /**
     * If the sender is console, then the result will be null.
     * Otherwise, it will be the player who executed the command.
     *
     * @param sender: the issuer of the command.
     * @return a list containing the player.
     */
    public static List<Player> getUserPlayer(CommandSender sender) {
        return (sender instanceof Player) ? new ArrayList<>(Collections.singleton((Player) sender)) : null;
    }

    /**
     * @param playerName: the name of the player.
     * @return a list containing the specified player.
     */
    public static List<Player> getPlayerFromName(String playerName) {
        playerName = playerName.toLowerCase().startsWith("player=") ? playerName.substring(7) : playerName;
        return new ArrayList<>(Collections.singleton(Bukkit.getPlayer(playerName)));
    }

    /**
     * @param worldName: the name of the world.
     * @return a list containing all the players in that world.
     * If it is null, it will return null.
     */
    public static List<Player> getPlayersFromWorld(String worldName) {
        worldName = worldName.toLowerCase().startsWith("world=") ? worldName.substring(6) : worldName;
        World world = Bukkit.getWorld(worldName);
        return (world == null) ? null : getPlayersFromWorld(world);
    }

    /**
     * @param world: a specified world.
     * @return a list containing all the players of that world.
     */
    public static List<Player> getPlayersFromWorld(World world) {
        return world.getPlayers();
    }

    /**
     * @param permission: the permission name (might be "op").
     * @return a list containing all the players who have the permission.
     */
    public static List<Player> getPlayersFromPermission(String permission) {
        permission = permission.toLowerCase().startsWith("perm=") ? permission.substring(5) : permission;
        if (permission.equalsIgnoreCase("op")) return getOPPlayers();
        List<Player> players = new ArrayList<>();
        for (Player p : getAllPlayers()) if (p.hasPermission(permission)) players.add(p);
        return players;
    }

    /**
     * @param permission: the permission.
     * @return a list containing all the players who have the permission.
     */
    public static List<Player> getPlayersFromPermission(Permission permission) {
        return getAllPlayers().stream().filter(p -> p.hasPermission(permission)).collect(Collectors.toList());
    }

    /**
     * @return a list containing all the server online operators.
     */
    public static List<Player> getOPPlayers() {
        return getAllPlayers().stream().filter(ServerOperator::isOp).collect(Collectors.toList());
    }

    /**
     * This will only work if Luck Perms is enabled.
     *
     * @param groupName: the name of the group.
     * @return a list containing all the players in that group.
     */
    public static List<Player> getPlayersFromGroup(String groupName) {
        List<Player> players = new ArrayList<>();
        if (!StrippedBroadcast.getPlugin(StrippedBroadcast.class).isLuckPermsEnabled()) return null;
        groupName = groupName.toLowerCase().startsWith("group=") ? groupName.substring(6) : groupName;
        for (Player p : getAllPlayers()) if (p.hasPermission("group." + groupName)) players.add(p);
        return players;
    }

    /**
     * @param effectName: the type of the effect.
     * @param level: the amplifier of the effect.
     * @return a list containing all the players that have an active effect
     * of type effectName and amplifier level.
     */
    public static List<Player> getPlayersFromEffect(String effectName, String level) {
        if (!StringUtils.isNumeric(level) || Integer.parseInt(level) < 0) return null;
        return getPlayersFromEffect(effectName, Integer.parseInt(level));
    }

    /**
     * @param effectName: the type of the effect.
     * @return a list containing all the players that have an active effect
     * of type effectName.
     */
    public static List<Player> getPlayersFromEffect(String effectName) {
        effectName = effectName.toLowerCase().startsWith("effect=") ? effectName.substring(7).toUpperCase() : effectName.toUpperCase();
        if (effectName.replace(" ", "").equalsIgnoreCase("")) return null;
        if (!MessagesUtil.isValidValue(new ArrayList<>(MessagesUtil.getPotionEffects()), effectName)) return null;
        return getPlayersFromEffect(PotionEffectType.getByName(effectName.toUpperCase()));
    }

    /**
     * @param effectName: the type of the effect.
     * @param level: the amplifier of the effect.
     * @return a list containing all the players that have an active effect
     * of type effectName and amplifier level.
     */
    public static List<Player> getPlayersFromEffect(String effectName, int level) {
        effectName = effectName.toLowerCase().startsWith("effect=") ? effectName.substring(7).toUpperCase() : effectName.toUpperCase();
        if (!MessagesUtil.isValidValue(new ArrayList<>(MessagesUtil.getPotionEffects()), effectName)) return null;
        return getPlayersFromEffect(PotionEffectType.getByName(effectName.toUpperCase()), level);
    }

    /**
     * @param effect: the type of the effect.
     * @param level: the amplifier of the effect.
     * @return a list containing all the players that have an active effect
     * of type effect and amplifier level.
     */
    public static List<Player> getPlayersFromEffect(PotionEffectType effect, int level) {
        return getAllPlayers().stream().filter(p ->
                p.getActivePotionEffects().stream().map(potionEffect -> new Object[]{potionEffect.getType(), level}).collect(Collectors.toList()).contains(new Object[]{effect, level})
        ).collect(Collectors.toList());
    }

    /**
     * @param effect: the type of the effect.
     * @return a list containing all the players that have an active effect
     * of type effect.
     */
    public static List<Player> getPlayersFromEffect(PotionEffectType effect) {
        return getAllPlayers().stream().filter(p -> p.getActivePotionEffects().stream().map(PotionEffect::getType).collect(Collectors.toList()).contains(effect)).collect(Collectors.toList());
    }

    /**
     * @param gameModeString: the name of the GameMode (might also be a number).
     * @return a list containing all the players in that GameMode.
     */
    public static List<Player> getPlayersFromGameMode(String gameModeString) {
        gameModeString = gameModeString.toLowerCase().startsWith("gamemode=") ? gameModeString.substring(9).toUpperCase() : gameModeString.toUpperCase();
        if (gameModeString.replace(" ", "").equalsIgnoreCase("")) return null;
        if (StringUtils.isNumeric(gameModeString)) {
            int gmInt = Integer.parseInt(gameModeString);
            if (gmInt > 0 && gmInt < 4) return getPlayersFromGameMode(GameMode.values()[gmInt]);
            else return null;
        } else {
            if (MessagesUtil.isValidValue(GameMode.values(), gameModeString)) return getPlayersFromGameMode(GameMode.valueOf(gameModeString));
            else return null;
        }
    }

    /**
     * @param gameMode: the GameMode.
     * @return a list containing all the players in that GameMode.
     */
    public static List<Player> getPlayersFromGameMode(GameMode gameMode) {
        return getAllPlayers().stream().filter(p -> p.getGameMode().equals(gameMode)).collect(Collectors.toList());
    }

    /**
     * This will only work if Vault Economy is enabled.
     *
     * @param money: the amount of money in string.
     * @return a list containing all the players with that amount of money on their balance.
     */
    public static List<Player> getPlayersFromMoney(String money) {
        if (!StrippedBroadcast.getPlugin(StrippedBroadcast.class).isVaultEnabled()) return null;
        money = money.toLowerCase().startsWith("money=") ? money.substring(6) : money;
        if (money.replace(" ", "").equalsIgnoreCase("")) return null;
        if (!NumberUtil.isDouble(money) || Double.parseDouble(money) < 0) return null;
        return getPlayersFromMoney(Double.parseDouble(money));
    }

    /**
     * This will only work if Vault Economy is enabled.
     *
     * @param money: the amount of money.
     * @return a list containing all the players with that amount of money on their balance.
     */
    public static List<Player> getPlayersFromMoney(Integer money) {
        return getPlayersFromMoney(Double.valueOf(money));
    }

    /**
     * This will only work if Vault Economy is enabled.
     *
     * @param money: the amount of money.
     * @return a list containing all the players with that amount of money on their balance.
     */
    public static List<Player> getPlayersFromMoney(Double money) {
        Economy economy = StrippedBroadcast.getPlugin(StrippedBroadcast.class).getEconomy();
        return getAllPlayers().stream().filter(p -> economy.getBalance(p) == money).collect(Collectors.toList());
    }

    /**
     * @param itemName: the name of the material of the item.
     * @param amount: the amount of the item.
     * @return a list containing all the player that have an item of type "material" and amount "amount" in their inventory.
     */
    public static List<Player> getPlayersFromItem(String itemName, String amount) {
        if (!StringUtils.isNumeric(amount) || Integer.parseInt(amount) < 0) return null;
        return getPlayersFromItem(itemName, Integer.parseInt(amount));
    }

    /**
     * @param itemName: the name of the material of the item.
     * @return a list containing all the player that have an item of type "material" in their inventory.
     */
    public static List<Player> getPlayersFromItem(String itemName) {
        itemName = itemName.startsWith("item=") ? itemName.toUpperCase().substring(5) : itemName.toUpperCase();
        if (itemName.replace(" ", "").equalsIgnoreCase("")) return null;
        if (!MessagesUtil.isValidValue(Material.values(), itemName)) return null;
        return getPlayersFromItem(Material.valueOf(itemName));
    }

    /**
     * @param itemName: the name of the material of the item.
     * @param amount: the amount of the item.
     * @return a list containing all the player that have an item of type "material" and amount "amount" in their inventory.
     */
    public static List<Player> getPlayersFromItem(String itemName, int amount) {
        itemName = itemName.startsWith("item=") ? itemName.toUpperCase().substring(5) : itemName.toUpperCase();
        if (itemName.replace(" ", "").equalsIgnoreCase("")) return null;
        if (!MessagesUtil.isValidValue(Material.values(), itemName)) return null;
        return getPlayersFromItem(Material.valueOf(itemName), amount);
    }

    /**
     * @param material: the material of the item.
     * @param amount: the amount of the item.
     * @return a list containing all the player that have an item of type "material" and amount "amount" in their inventory.
     */
    public static List<Player> getPlayersFromItem(Material material, int amount) {
        return getAllPlayers().stream().filter(p -> p.getInventory().contains(new ItemStack(material, amount))).collect(Collectors.toList());
    }

    /**
     * @param material: the type of the item.
     * @return a list containing all the player that have an item with the material in their inventory.
     */
    public static List<Player> getPlayersFromItem(Material material) {
        return getAllPlayers().stream().filter(p ->
                Arrays.stream(getAllPlayerContents(p)).map(ItemStack::getType).collect(Collectors.toList()).contains(material)
        ).collect(Collectors.toList());
    }

    /**
     * @param player: the player.
     * @return an array containing all the contents of the player's inventory
     * (including armor and extra contents if server is above 1.8).
     */
    private static ItemStack[] getAllPlayerContents(Player player) {
        List<ItemStack> contents = new ArrayList<>(Arrays.asList(player.getInventory().getContents()));
        contents.addAll(Arrays.asList(player.getInventory().getArmorContents()));
        if (!StrippedBroadcast.getPlugin(StrippedBroadcast.class).isServer1_8())
            contents.addAll(Arrays.asList(player.getInventory().getExtraContents()));
        return contents.toArray(new ItemStack[0]);
    }
}