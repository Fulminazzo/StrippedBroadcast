package it.fulminazzo.sbcb.Utils;


import it.angrybear.Utils.NumberUtils;
import it.fulminazzo.Utils.StringsUtil;
import it.fulminazzo.sbcAPI.PlayersUtilB;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class MessagesUtilB {
    /**
     * Parses the command string the user gives. This should respect the
     * following criteria:
     * - parenthesis should already be removed ["(world=world || world=world_the_end)" should become "world=world || world=world_the_end"];
     * - only '&&' and '||' are accepted (same functioning as in java);
     * - valid values are explained in #parsedCommand.
     *
     * @param commandString: the string that should be parsed.
     * @param player: the player that should return with the keyword "me".
     *
     * @return playersList: the list of all valid players according to the commandString.
     */
    public List<ProxiedPlayer> parseCommands(String commandString, ProxiedPlayer player) {
        List<ProxiedPlayer> players = new ArrayList<>();
        List<Object[]> commands = new ArrayList<>();
        List<String> commandStrings = new ArrayList<>();
        List<String> subCommands = new ArrayList<>();
        // TODO: Implement errors system?
        List<String> errors = new ArrayList<>();

        String[] subCommandsStrings = StringsUtil.getCommandsFromParenthesis(commandString);
        if (subCommandsStrings.length != 0) {
            for (String s : subCommandsStrings) {
                subCommands.add(s.replace("(", "").replace(")", ""));
                commandString = commandString.replace("(" + s + ")", String.valueOf(subCommands.indexOf(s)));
            }
        }
        for (String c : Arrays.stream(commandString.split("&& ")).map(c -> "&& " + c).collect(Collectors.toList())) commandStrings.addAll(Arrays.asList(c.split("\\|\\| ")));
        if (commandStrings.size() > 1 && commandStrings.size() != subCommands.size()) {
            players.addAll(parseCommand(commandStrings.get(0).replace("&& ", "").replace(" ", ""), player));
            commandStrings.remove(0);
        }
        commandStrings.forEach(c -> commands.add(new Object[]{c.replace("&& ", ""), c.startsWith("&& ")}));

        for (Object[] cmds : commands) {
            String cmd = ((String) cmds[0]).replace(" ", "");
            Boolean isAnd = (Boolean) cmds[1];
            List<ProxiedPlayer> commandPlayers = NumberUtils.isInteger(cmd) ?
                    parseCommands(subCommands.get(Integer.parseInt(cmd)), player) : parseCommand(cmd, player);
            if (commandPlayers == null) {
                errors.add(cmd);
                continue;
            }
            if (!isAnd) {
                for (ProxiedPlayer p : commandPlayers) if (!players.contains(p)) players.add(p);
            } else {
                List<ProxiedPlayer> newPlayers = new ArrayList<>();
                if (players.isEmpty() && commandStrings.size() > 1) newPlayers = commandPlayers;
                else for (ProxiedPlayer p : commandPlayers) if (players.contains(p)) newPlayers.add(p);
                players = newPlayers;
            }
        }
        return players;
    }

    /**
     * Parses a single given command (for example "all" or "server=<server>").
     *
     * @param command: the command string.
     * @param player: the player that should return with keyword "me" (might be null).
     *
     * @return playersList: the list of all valid players according to command.
     */
    public List<ProxiedPlayer> parseCommand(String command, ProxiedPlayer player) {
        command = command.toLowerCase().replace(" ", "");
        if (command.replace(" ", "").equalsIgnoreCase("")) {
            return null;
        } else if (command.equalsIgnoreCase("all")) {
            // "all": returns all the online players.
            return PlayersUtilB.getAllPlayers();
        } else if (command.equalsIgnoreCase("me")) {
            // "me": returns the sender of the command.
            return PlayersUtilB.getUserPlayer(player);
        } else if (command.startsWith("server=")) {
            // "server=<server>": returns every player from that server.
            return PlayersUtilB.getPlayersFromServer(command);
        } else if (command.startsWith("perm=")) {
            // "perm=<permission>": returns every player from that has permission <permission>.
            return PlayersUtilB.getPlayersFromPermission(command);
        } else if (command.startsWith("group=")) {
            // Only works with Luck Perms.
            // "group=<group>": returns every player that is in the group <group>.
            return PlayersUtilB.getPlayersFromGroup(command);
        } else if (command.startsWith("player=") || ProxyServer.getInstance().getPlayer(command) != null) {
            // "player=<playerName>" or "<playerName>: returns the player called <playerName>
            return PlayersUtilB.getPlayerFromName(command);
        }
        return null;
    }

    /**
     * An alias for #parseString that creates a TextComponent.
     *
     * @param string: the string that should be converted.
     *
     * @return string: the converted string.
     */
    public TextComponent parseTextComponent(String string) {
        return new TextComponent(StringsUtil.parseString(string));
    }
}