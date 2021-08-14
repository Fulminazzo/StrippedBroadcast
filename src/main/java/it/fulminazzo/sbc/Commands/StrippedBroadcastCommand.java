package it.fulminazzo.sbc.Commands;

import it.fulminazzo.sbc.StrippedBroadcast;
import it.fulminazzo.sbc.Utils.StringsUtil;
import net.luckperms.api.LuckPerms;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.util.StringUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class StrippedBroadcastCommand implements TabExecutor {
    private final StrippedBroadcast plugin;

    /**
     * Constructor of the StrippedBroadcastCommand.
     *
     * @param plugin: the main class of the plugin.
     */
    public StrippedBroadcastCommand(StrippedBroadcast plugin) {
        this.plugin = plugin;
    }

    /**
     * Command listener of the StrippedBroadcastCommand. It checks for
     * valid inputs and targets and sends the message.
     *
     * @param sender: the command sender.
     * @param command: the command.
     * @param label: the command name.
     * @param args: the command arguments.
     */
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        StringsUtil stringsUtil = plugin.getStringsUtil();
        Player player = (sender instanceof Player) ? (Player) sender : null;
        List<Player> players;
        if (args.length == 0) {
            sendHelpMessage(sender);
            return true;
        }
        String[] possibleCommands = stringsUtil.getCommandsFromParenthesis(stringsUtil.getParsedMessage(args, false));
        if (possibleCommands.length == 0) {
            players = stringsUtil.parseCommand(args[0], player);
        } else {
            players = stringsUtil.parseCommands(possibleCommands[0], player);
            args = stringsUtil.getParsedMessage(args, false).replace(possibleCommands[0], "").split(" ");
        }
        if (players == null) {
            sendHelpMessage(sender);
            return true;
        }
        List<String> arrayList = new ArrayList<>();
        for (String s : args) if (!(s.equalsIgnoreCase(args[0]))) arrayList.add(s);
        if (arrayList.isEmpty()) {
            sendHelpMessage(sender);
            return true;
        }
        if (!plugin.isLuckPermsEnabled() &&
                (possibleCommands.length == 0 ? stringsUtil.getParsedMessage(args, false) : stringsUtil.getParsedMessage(possibleCommands, false)).contains("group="))
            sender.sendMessage(stringsUtil.parseString(String.format("&e%s &8» &cThe keyword \"group=\" was detected, but LuckPerms was not found.", plugin.getName())));
        if (players.isEmpty())
            sender.sendMessage(stringsUtil.parseString(String.format("&e%s &8» &cNo player was found after executing the command. Are you sure you put valid values?", plugin.getName())));
        StrippedBroadcast.sendStrippedBroadcast(players, arrayList);
        return true;
    }

    /**
     * TabComplete listener for StrippedBroadcastCommand. It returns a list of
     * valid inputs for the command according to the index of the argument the
     * sender is currently typing.
     *
     * @param sender: the command sender.
     * @param command: the command.
     * @param label: the command name.
     * @param args: the command arguments.
     *
     * @return list: the list of inputs.
     */
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        StringsUtil stringsUtil = plugin.getStringsUtil();
        Player player = (sender instanceof Player) ? (Player) sender : null;
        List<String> list = new ArrayList<>();
        String[] possibleCommands = stringsUtil.getCommandsFromParenthesis(stringsUtil.getParsedMessage(args, false));
        if (args.length == 1 || (possibleCommands.length == 0 && stringsUtil.parseCommand(args[0], player) == null)) {
            String arguments = stringsUtil.getParsedMessage(args, false);
            if (stringsUtil.isOpenParenthesis(arguments) != 0) {
                String lastInput = stringsUtil.removeParenthesis(args[args.length - 1]);
                if (args.length != 1 && lastInput.equalsIgnoreCase("")) lastInput = stringsUtil.removeParenthesis(args[args.length - 2]);
                // Since Bukkit is able to detect an online player just by his initial,
                // the player part of this code makes sure that the sender will receive the whole player name.
                if (stringsUtil.parseCommand(lastInput, player) != null && !lastInput.endsWith("=") && (Bukkit.getPlayer(lastInput.replace("player=", "")) == null ||
                                Bukkit.getPlayer(lastInput.replace("player=", "")).getName().equalsIgnoreCase(lastInput.replace("player=", "")))) {
                    list.add("&&");
                    list.add("||");
                } else {
                    for (String s : getInputOptions(lastInput))
                        list.add((args[args.length - 1].startsWith("(") ? args[args.length - 1].replace(lastInput, "") : "") +
                                s + (s.toLowerCase().endsWith("=") ? "" : stringsUtil.repeat(')', stringsUtil.isOpenParenthesis(arguments))));
                }
            } else {
                list = getInputOptions(args[0]);
            }
        } else {
            if (possibleCommands.length != 0) {
                args = stringsUtil.getParsedMessage(args, false).replace(possibleCommands[0], "").split(" ");
                if (args.length == 1) {
                    args = Arrays.copyOf(args, 2);
                    args[1] = "";
                }
            }
            if (args.length == 2) {
                if (args[1].startsWith("[")) list.add("[RAINBOW]");
                list.add("<message>");
            }
            if (args.length >= 2) list.addAll(plugin.getHexColorsUtil().getFormattedHexList(
                    plugin.getStringsUtil().getParsedMessage(Arrays.asList(Arrays.copyOfRange(args, args.length - 1, args.length)), false)));
        }
        return StringUtil.copyPartialMatches(args[args.length-1], list, new ArrayList<>());
    }

    /**
     * Returns a list containing all available options starting from the input.
     *
     * @param input: the string to start from.
     *
     * @return list: the list.
     */
    public List<String> getInputOptions(String input) {
        List<String> list = Bukkit.getOnlinePlayers().stream().map(HumanEntity::getName).collect(Collectors.toList());
        list.add("all");
        list.add("perm=");
        if (plugin.isLuckPermsEnabled()) list.add("group=");
        list.add("world=");
        list.add("player=");
        if (input.toLowerCase().startsWith("world=")) list.addAll(Bukkit.getWorlds().stream().map(world -> "world=" + world.getName()).collect(Collectors.toList()));
        if (input.toLowerCase().startsWith("player=")) list.addAll(Bukkit.getOnlinePlayers().stream().map(player -> "player=" + player.getName()).collect(Collectors.toList()));
        if (input.toLowerCase().startsWith("perm=")) {
            list.add("perm=op");
            list.addAll(Bukkit.getPluginManager().getPermissions().stream().map(permission -> "perm=" + permission.getName()).collect(Collectors.toList()));
        }
        if (input.toLowerCase().startsWith("group=") && plugin.isLuckPermsEnabled()) {
            list.addAll(plugin.getLuckPerms().getGroupManager().getLoadedGroups().stream().map(group -> "group=" + group.getName().toLowerCase()).collect(Collectors.toList()));
        }
        return list;
    }

    /**
     * Sends help message to the command sender.
     *
     * @param sender: the command sender.
     */
    private void sendHelpMessage(CommandSender sender) {
        String pluginName = plugin.getName();
        sender.sendMessage(plugin.getStringsUtil().parseString(
                String.format("&e%s &8» &cUsage: /%s (syntax)/<player>/world=<world>/perm=<permission>/me/all <message>", pluginName, pluginName.toLowerCase())));
    }
}