package it.fulminazzo.sbcb.Commands;

import it.fulminazzo.Utils.StringsUtil;
import it.fulminazzo.sbcAPI.PlayersUtilB;
import it.fulminazzo.sbcb.StrippedBroadcastBungee;
import it.fulminazzo.sbcb.Utils.MessagesUtilB;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;
import net.md_5.bungee.api.plugin.TabExecutor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

public class StrippedBroadcastBCommand extends Command implements TabExecutor {
    private final StrippedBroadcastBungee plugin;
    private final MessagesUtilB messagesUtilB;

    public StrippedBroadcastBCommand(StrippedBroadcastBungee plugin) {
        super("strippedbroadcastb", "sbcb.main", "sbcb, strippedbroadcastb, sbroadcastb, strippedbcb");
        this.plugin = plugin;
        messagesUtilB = plugin.getStringsUtilB();
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        ProxiedPlayer player = (sender instanceof ProxiedPlayer) ? (ProxiedPlayer) sender : null;
        List<ProxiedPlayer> players;
        if (args.length == 0) {
            issueNoMessageSupplied(sender, PlayersUtilB.getAllPlayers());
            return;
        }
        String[] possibleCommands = StringsUtil.getCommandsFromParenthesis(StringsUtil.getParsedMessage(args, false));
        if (possibleCommands.length == 0) {
            players = messagesUtilB.parseCommand(args[0], player);
        } else {
            players = messagesUtilB.parseCommands(possibleCommands[0], player);
            args = StringsUtil.getParsedMessage(args, false).replace(possibleCommands[0], "").split(" ");
        }
        if (players == null) {
            sendHelpMessage(sender);
            return;
        }
        List<String> arrayList = new ArrayList<>();
        for (String s : args) if (!(s.equalsIgnoreCase(args[0]))) arrayList.add(s);
        if (arrayList.isEmpty()) {
            issueNoMessageSupplied(sender, players);
            return;
        }
        if (!StrippedBroadcastBungee.isLuckPermsEnabled() &&
                (possibleCommands.length == 0 ? StringsUtil.getParsedMessage(args, false) :
                        StringsUtil.getParsedMessage(possibleCommands, false)).contains("group="))
            sender.sendMessage(messagesUtilB.parseTextComponent(getMessage("&e%s &8» &cThe keyword \"group=\" was detected, but LuckPerms was not found."))
            );
        if (players.isEmpty())
            sender.sendMessage(messagesUtilB.parseTextComponent(
                    getMessage("&e%s &8» &cNo player was found after executing &cthe command. Are you sure you put valid values?")));
        StrippedBroadcastBungee.sendStrippedBroadcast(players, arrayList);
    }

    @Override
    public Iterable<String> onTabComplete(CommandSender sender, String[] args) {
        ProxiedPlayer player = (sender instanceof ProxiedPlayer) ? (ProxiedPlayer) sender : null;
        List<String> list = new ArrayList<>();
        String[] possibleCommands = StringsUtil.getCommandsFromParenthesis(StringsUtil.getParsedMessage(args, false));
        if (args.length == 1 || (possibleCommands.length == 0 && messagesUtilB.parseCommand(args[0], player) == null)) {
            String arguments = StringsUtil.getParsedMessage(args, false);
            if (StringsUtil.isOpenParenthesis(arguments) != 0) {
                String lastInput = StringsUtil.removeParenthesis(args[args.length - 1]);
                if (args.length != 1 && lastInput.equalsIgnoreCase("")) lastInput = StringsUtil.removeParenthesis(args[args.length - 2]);
                // Since Bungeecord is able to detect an online player just by his initial,
                // the player part of this code makes sure that the sender will receive the whole player name.
                if (messagesUtilB.parseCommand(lastInput, player) != null && !lastInput.endsWith("=") &&
                        (plugin.getProxy().getPlayer(lastInput.replace("player=", "")) == null ||
                        plugin.getProxy().getPlayer(lastInput.replace("player=", "")).getName().equalsIgnoreCase(
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
            if (args.length == 2) list.add("<message>");
        }
        return StringsUtil.copyPartialMatches(args[args.length-1], list, new ArrayList<>());
    }

    /**
     * Handles when a sender specifies the users, but does not specify
     * any message.
     *
     * @param sender: the sender.
     * @param players: the players supplied.
     */
    private void issueNoMessageSupplied(CommandSender sender, List<ProxiedPlayer> players) {
        ProxiedPlayer player = (sender instanceof ProxiedPlayer) ? (ProxiedPlayer) sender : null;
        if (player == null) sendHelpMessage(sender);
        else {
            plugin.flipPlayerBroadcasting(player, players);
            String message;
            if (plugin.isPlayerBroadcasting(player))
                message = "&e%s &8» &aNow your messages will be broadcasted &ato the specified players.";
            else
                message = "&e%s &8» &cYou are not broadcasting anymore.";
            player.sendMessage(messagesUtilB.parseTextComponent(getMessage(message)));
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
        ProxyServer proxy = plugin.getProxy();
        Collection<ProxiedPlayer> players = proxy.getPlayers();
        List<String> list = players.stream().map(ProxiedPlayer::getName).collect(Collectors.toList());
        list.add("all");
        list.add("perm=");
        if (StrippedBroadcastBungee.isLuckPermsEnabled()) list.add("group=");
        list.add("player=");
        list.add("server=");
        if (input.startsWith("player=")) list.addAll(players.stream().map(player -> "player=" + player.getName()).collect(Collectors.toList()));
        if (input.startsWith("group=") && StrippedBroadcastBungee.isLuckPermsEnabled()) {
            list.addAll(plugin.getLuckPerms().getGroupManager().getLoadedGroups().stream().map(group -> "group=" + group.getName().toLowerCase()).collect(Collectors.toList()));
        }
        if (input.startsWith("server=")) list.addAll(proxy.getServers().keySet().stream().map(server -> "server=" + server.toLowerCase()).collect(Collectors.toList()));
        return list;
    }

    /**
     * Sends help message to the command sender.
     *
     * @param sender: the command sender.
     */
    private void sendHelpMessage(CommandSender sender) {
        sender.sendMessage(messagesUtilB.parseTextComponent(
                getMessage("&e%s &8» &cUsage: /%s&c (syntax)/<player>/server=<server>/perm=<permission>/me/all <message>")
        ));
    }

    /**
     * Sends a message with the plugin prefix.
     *
     * @param message: the message.
     *
     * @return the parsed message.
     */
    private String getMessage(String message) {
        String pluginName = "StrippedBroadcast&bB";
        return StringsUtil.parseString(String.format(message, pluginName, pluginName.toLowerCase()));
    }
}