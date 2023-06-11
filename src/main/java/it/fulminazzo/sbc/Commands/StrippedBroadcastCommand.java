package it.fulminazzo.sbc.Commands;

import it.fulminazzo.Utils.StringsUtil;
import it.fulminazzo.sbc.Enums.MessageType;
import it.fulminazzo.sbc.StrippedBroadcast;
import it.fulminazzo.sbc.Utils.MessagesUtil;
import it.fulminazzo.sbcAPI.PlayersUtil;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.util.StringUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class StrippedBroadcastCommand implements TabExecutor {
    private final StrippedBroadcast plugin;
    private final MessagesUtil messagesUtil;
    private final List<String> modes = Arrays.stream(MessageType.values()).map(Enum::name).map(String::toLowerCase).collect(Collectors.toList());

    /**
     * Constructor of the StrippedBroadcastCommand.
     *
     * @param plugin: the main class of the plugin.
     */
    public StrippedBroadcastCommand(StrippedBroadcast plugin) {
        this.plugin = plugin;
        messagesUtil = plugin.getStringsUtil();
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
        Player player = (sender instanceof Player) ? (Player) sender : null;
        List<Player> players;
        MessageType messageType = MessageType.MESSAGE;
        if (args.length == 0 || args[0].equalsIgnoreCase("[RAINBOW]")) {
            issueNoMessageSupplied(sender, PlayersUtil.getAllPlayers(), messageType,
                    args.length > 0 && args[0].equalsIgnoreCase("[RAINBOW]"));
            return true;
        }
        String[] possibleCommands = StringsUtil.getCommandsFromParenthesis(StringsUtil.getParsedMessage(args, false));
        if (possibleCommands.length == 0) {
            players = messagesUtil.parseCommand(args[0], player);
        } else {
            players = messagesUtil.parseCommands(possibleCommands[0], player);
            args = StringsUtil.getParsedMessage(args, false).replace(possibleCommands[0], "").split(" ");
        }
        if (players == null) {
            sendHelpMessage(sender);
            return true;
        }
        List<String> arrayList = new ArrayList<>();
        for (String s : args) if (!(s.equalsIgnoreCase(args[0]))) arrayList.add(s);
        String firstElem = arrayList.isEmpty() ? "" : arrayList.get(0);
        if (modes.contains(firstElem.toLowerCase())) {
            arrayList.remove(0);
            messageType = MessageType.valueOf(firstElem.toUpperCase());
            firstElem = arrayList.isEmpty() ? "" : arrayList.get(0);
        }
        boolean isRainbow = !arrayList.isEmpty() && firstElem.equalsIgnoreCase("[RAINBOW]");
        if (arrayList.isEmpty() || (arrayList.size() == 1 && isRainbow)) {
            issueNoMessageSupplied(sender, players, messageType, isRainbow);
            return true;
        }
        if (!plugin.isLuckPermsEnabled() &&
                (possibleCommands.length == 0 ? StringsUtil.getParsedMessage(args, false) :
                        StringsUtil.getParsedMessage(possibleCommands, false)).contains("group="))
            sender.sendMessage(getMessage("&e%s &8» &cThe keyword \"group=\" was detected, but LuckPerms was not found."));
        if (players.isEmpty())
            sender.sendMessage(getMessage("&e%s &8» &cNo player was found after executing the command. Are you sure you put valid values?"));
        StrippedBroadcast.sendStrippedBroadcast(players, messageType, arrayList);
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
        Player player = (sender instanceof Player) ? (Player) sender : null;
        List<String> list = new ArrayList<>();
        String[] possibleCommands = StringsUtil.getCommandsFromParenthesis(StringsUtil.getParsedMessage(args, false));
        if (args.length == 1 || (possibleCommands.length == 0 && messagesUtil.parseCommand(args[0], player) == null)) {
            String arguments = StringsUtil.getParsedMessage(args, false);
            if (StringsUtil.isOpenParenthesis(arguments) != 0) {
                String lastInput = StringsUtil.removeParenthesis(args[args.length - 1]);
                if (args.length != 1 && lastInput.equalsIgnoreCase("")) lastInput = StringsUtil.removeParenthesis(args[args.length - 2]);
                // Since Bukkit is able to detect an online player just by his initial,
                // the player part of this code makes sure that the sender will receive the whole player name.
                if (messagesUtil.parseCommand(lastInput, player) != null && !lastInput.endsWith("=") && (Bukkit.getPlayer(
                        lastInput.replace("player=", "")) == null ||
                                Bukkit.getPlayer(lastInput.replace("player=", "")).getName().equalsIgnoreCase(
                                        lastInput.replace("player=", "")))) {
                    list.add("&&");
                    list.add("||");
                } else {
                    for (String s : getInputOptions(lastInput))
                        list.add((args[args.length - 1].startsWith("(") ? args[args.length - 1].replace(lastInput, "") : "") +
                                s + (s.toLowerCase().endsWith("=") ? "" : StringsUtil.repeat(')', StringsUtil.isOpenParenthesis(arguments))));
                }
            } else {
                list = getInputOptions(args[0]);
            }
        } else {
            if (possibleCommands.length != 0) {
                args = StringsUtil.getParsedMessage(args, false).replace(possibleCommands[0], "").split(" ");
                if (args.length == 1) {
                    args = Arrays.copyOf(args, 2);
                    args[1] = "";
                }
            }
            if (args.length == 2) {
                if (args[1].startsWith("[")) list.add("[RAINBOW]");
                list.add("<message>");
                list.addAll(modes);
            }
            if (args.length == 3 && modes.contains(args[1].toLowerCase())) {
                if (args[2].startsWith("[")) list.add("[RAINBOW]");
                list.add("<message>");
            }
            if (args.length >= 2) {
                list.addAll(plugin.getHexColorsUtil().getFormattedHexList(
                        StringsUtil.getParsedMessage(Arrays.asList(Arrays.copyOfRange(args, args.length - 1, args.length)),
                                false)));
            }
        }
        return StringUtil.copyPartialMatches(args[args.length-1], list, new ArrayList<>());
    }

    /**
     * Handles when a sender specifies the users, but does not specify
     * any message.
     *
     * @param sender: the sender.
     * @param players: the players supplied.
     * @param messageType: the type of the message.
     * @param rainbow: check if the message starts with "[RAINBOW]"
     */
    private void issueNoMessageSupplied(CommandSender sender, List<Player> players, MessageType messageType, Boolean rainbow) {
        Player player = (sender instanceof Player) ? (Player) sender : null;
        if (player == null) sendHelpMessage(sender);
        else {
            plugin.flipPlayerBroadcasting(player, players, messageType, rainbow);
            String message;
            if (plugin.isPlayerBroadcasting(player)) {
                message = "&e%s &8» &aNow your messages will be broadcasted to the specified players.";
            } else
                message = "&e%s &8» &cYou are not broadcasting anymore.";
            player.sendMessage(getMessage(message));
        }
    }

    /**
     * Returns a list containing all available options starting from the input.
     *
     * @param input: the string to start from.
     *
     * @return list: the list.
     */
    public List<String> getInputOptions(String input) {
        input = input.toLowerCase();
        Collection<? extends Player> players = Bukkit.getOnlinePlayers();
        List<String> list = players.stream().map(HumanEntity::getName).collect(Collectors.toList());
        list.add("all");
        list.add("perm=");
        if (plugin.isLuckPermsEnabled()) list.add("group=");
        list.add("world=");
        list.add("player=");
        list.add("item=");
        list.add("effect=");
        list.add("gamemode=");
        if (plugin.isVaultEnabled()) list.add("money=");
        if (input.startsWith("world=")) list.addAll(Bukkit.getWorlds().stream().map(world -> "world=" + world.getName()).collect(Collectors.toList()));
        if (input.startsWith("player=")) list.addAll(players.stream().map(player -> "player=" + player.getName()).collect(Collectors.toList()));
        if (input.startsWith("perm=")) {
            list.add("perm=op");
            list.addAll(Bukkit.getPluginManager().getPermissions().stream().map(permission -> "perm=" + permission.getName()).collect(Collectors.toList()));
        }
        if (input.startsWith("group=") && plugin.isLuckPermsEnabled()) {
            list.addAll(plugin.getLuckPerms().getGroupManager().getLoadedGroups().stream().map(group -> "group=" + group.getName().toLowerCase()).collect(Collectors.toList()));
        }
        boolean notValidInput = input.replace(",", "").length() != input.length() - 1 && input.replace(",", "").length() != input.length();
        if (input.startsWith("item=")) {
            if (notValidInput) return list;
            if (input.contains(",")) {
                String finalInput = input;
                list.addAll(Arrays.stream(new int[]{1, 2, 3, 4, 5, 6, 7, 8, 9, 0}).mapToObj(i -> finalInput + i).collect(Collectors.toList()));
            } else list.addAll(Arrays.stream(Material.values()).map(m -> "item=" + m.toString().toLowerCase()).collect(Collectors.toList()));
        }
        if (input.startsWith("effect=")) {
            if (notValidInput) return list;
            if (input.contains(",")) {
                String finalInput = input;
                list.addAll(Arrays.stream(new int[]{1, 2, 3, 4, 5, 6, 7, 8, 9, 0}).mapToObj(i -> finalInput + i).collect(Collectors.toList()));
            } else list.addAll(MessagesUtil.getPotionEffects().stream().map(p -> "effect=" + p).collect(Collectors.toList()));
        }
        if (input.startsWith("gamemode=")) {
            list.addAll(Stream.concat(
                    Arrays.stream(new int[]{0, 1, 2, 3}).mapToObj(String::valueOf),
                    Arrays.stream(GameMode.values()).map(g -> g.toString().toLowerCase())
            ).map(s -> "gamemode=" + s).collect(Collectors.toList()));
        }
        return list;
    }

    /**
     * Sends help message to the command sender.
     *
     * @param sender: the command sender.
     */
    private void sendHelpMessage(CommandSender sender) {
        sender.sendMessage(getMessage("&e%s &8» &cUsage: /%s (syntax)/<player>/world=<world>/perm=<permission>/me/all <message>"));
    }

    /**
     * Sends a message with the plugin prefix.
     *
     * @param message: the message.
     *
     * @return the parsed message.
     */
    private String getMessage(String message) {
        String pluginName = plugin.getName();
        return StringsUtil.parseString(String.format(message, pluginName, pluginName.toLowerCase()));
    }
}