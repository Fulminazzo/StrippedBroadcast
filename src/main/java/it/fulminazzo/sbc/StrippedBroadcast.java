package it.fulminazzo.sbc;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.StringUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public final class StrippedBroadcast extends JavaPlugin implements TabExecutor {
    public void onEnable() {
        getCommand("sbc").setExecutor(this);
        getCommand("sbc").setTabCompleter(this);
    }

    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        List<Player> players;
        if (args.length == 0) {
            sendHelpMessage(sender);
            return true;
        }
        String firstArg = args[0];
        if (firstArg.equalsIgnoreCase("all")) {
            players = new ArrayList<>(Bukkit.getOnlinePlayers());
        } else if (Bukkit.getPlayer(firstArg) != null) {
            players = Collections.singletonList(Bukkit.getPlayer(firstArg));
        } else if (Bukkit.getWorld(firstArg) != null) {
            players = Bukkit.getWorld(firstArg).getPlayers();
        } else if (firstArg.toLowerCase().startsWith("perm=")) {
            String permission = firstArg.substring(5);
            players = Bukkit.getOnlinePlayers().stream().filter(player -> player.hasPermission(permission)).collect(Collectors.toList());
        } else {
            sendHelpMessage(sender);
            return true;
        }
        List<String> arrayList = Arrays.stream(args).filter(string -> !string.equalsIgnoreCase(args[0])).collect(Collectors.toList());
        if (arrayList.isEmpty()) {
            sendHelpMessage(sender);
            return true;
        }
        sendStrippedBroadcast(players, arrayList);
        return true;
    }

    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        List<String> list = new ArrayList<>();
        if (args.length == 1) {
            list.add("all");
            list.add("perm=");
            list = Bukkit.getOnlinePlayers().stream().map(HumanEntity::getName).collect(Collectors.toList());
            if (StringUtil.copyPartialMatches(args[0], list, new ArrayList<>()).isEmpty())
                list = Bukkit.getWorlds().stream().map(World::getName).collect(Collectors.toList());
            if (args[0].toLowerCase().startsWith("perm="))
                list = Bukkit.getPluginManager().getPermissions().stream().map(permission -> "perm=" + permission.getName()).collect(Collectors.toList());
        }
        if (args.length == 2) list.add("<message>");
        return StringUtil.copyPartialMatches(args[args.length-1], list, new ArrayList<>());
    }

    private static String parseString(String string) {
        return string.replace("&", "§");
    }

    private void sendHelpMessage(CommandSender sender) {
        String pluginName = this.getName();
        sender.sendMessage(parseString(String.format("&e%s &8» &cUsage: /%s <player>/<world>/perm=<permission>/all <message>", pluginName, pluginName.toLowerCase())));
    }

    public static void sendStrippedBroadcast(List<Player> players, String[] strings) {
        sendStrippedBroadcast(players, Arrays.asList(strings));
    }

    public static void sendStrippedBroadcast(List<Player> players, List<String> list) {
        String message = "";
        for (String string : list) message += parseString(string) + " ";
        sendStrippedBroadcast(players, message);
    }

    public static void sendStrippedBroadcast(List<Player> players, String message) {
        StrippedBroadcastEvent event = new StrippedBroadcastEvent(players, message);
        Bukkit.getConsoleSender().sendMessage(message);
        for (Player p : players) p.sendMessage(message);
        Bukkit.getServer().getPluginManager().callEvent(event);
    }
}
