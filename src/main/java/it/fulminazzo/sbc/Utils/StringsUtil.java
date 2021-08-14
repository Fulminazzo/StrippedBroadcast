package it.fulminazzo.sbc.Utils;

import it.fulminazzo.sbc.StrippedBroadcast;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.permissions.ServerOperator;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class StringsUtil {
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
    public List<Player> parseCommands(String commandString, Player player) {
        List<Player> players = new ArrayList<>();
        List<Object[]> commands = new ArrayList<>();
        List<String> commandStrings = new ArrayList<>();
        List<String> subCommands = new ArrayList<>();
        // TODO: Implement errors system?
        List<String> errors = new ArrayList<>();

        String[] subCommandsStrings = getCommandsFromParenthesis(commandString);
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
            List<Player> commandPlayers = StringUtils.isNumeric(cmd) ?
                    parseCommands(subCommands.get(Integer.parseInt(cmd)), player) : parseCommand(cmd, player);
            if (commandPlayers == null) {
                errors.add(cmd);
                continue;
            }
            if (!isAnd) {
                for (Player p : commandPlayers) if (!players.contains(p)) players.add(p);
            } else {
                List<Player> newPlayers = new ArrayList<>();
                if (players.isEmpty() && commandStrings.size() > 1) newPlayers = commandPlayers;
                else for (Player p : commandPlayers) if (players.contains(p)) newPlayers.add(p);
                players = newPlayers;
            }
        }
        return players;
    }

    /**
     * Parses a single given command. This should be:
     * - all (all players);
     * - me (the player executing the command);
     * - <player> (a player);
     * - player=<player> (a player);
     * - world=<world> (all players of a world);
     * - perm=<perm> (all players having this permission).
     *
     * @param command: the command string.
     * @param player: the player that should return with keyword "me".
     *
     * @return playersList: the list of all valid players according to command.
     */
    public List<Player> parseCommand(String command, Player player) {
        List<Player> players = null;
        command = command.toLowerCase().replace(" ", "");
        if (command.equalsIgnoreCase("all")) {
            players = new ArrayList<>(Bukkit.getOnlinePlayers());
        } else if (command.equalsIgnoreCase("me")) {
            if (player != null) players = new ArrayList<>(Collections.singleton(player));
        } else if (command.replace(" ", "").equalsIgnoreCase("")) {
            return null;
        } else if (command.startsWith("world=")) {
            World world = Bukkit.getWorld(command.substring(6));
            if (world != null) players = world.getPlayers();
        } else if (command.startsWith("perm=")) {
            String permission = command.substring(5);
            players = permission.equalsIgnoreCase("op") ? Bukkit.getOnlinePlayers().stream().filter(ServerOperator::isOp).collect(Collectors.toList()) :
                    Bukkit.getOnlinePlayers().stream().filter(p -> p.hasPermission(permission)).collect(Collectors.toList());
        } else if (command.startsWith("group=")) {
            StrippedBroadcast plugin = StrippedBroadcast.getPlugin(StrippedBroadcast.class);
            players = new ArrayList<>();
            if (plugin.isLuckPermsEnabled()) {
                String group = command.substring(6);
                players = Bukkit.getOnlinePlayers().stream().filter(p -> p.hasPermission("group." + group)).collect(Collectors.toList());
            }
        } else if (command.startsWith("player=")) {
            String playerName = command.substring(7);
            if (Bukkit.getPlayer(playerName) != null) players = new ArrayList<>(Collections.singleton(Bukkit.getPlayer(playerName)));
        } else if (Bukkit.getPlayer(command) != null) {
            players = new ArrayList<>(Collections.singleton(Bukkit.getPlayer(command)));
        }
        return players;
    }

    /**
     * Creates an array containing every command given in parentheses.
     * For example: "(world=world || world=world_the_end) && (perm=bukkit.*)"
     * becomes: ["world=world || world=world_the_end", "perm=bukkit"]
     *
     * @param command: the command string.
     *
     * @return commands: an array of commands.
     */
    public String[] getCommandsFromParenthesis(String command) {
        int leftPos = -1;
        int parenthesisCount = 0;
        List<String> commands = new ArrayList<>();
        for (int i = 0; i < command.length(); i++) {
            char c = command.charAt(i);
            if (c == '(') {
                parenthesisCount++;
                if (leftPos == -1) leftPos = i;
            }
            if (c == ')') {
                if (parenthesisCount == 1 && leftPos != -1) {
                    commands.add(command.substring(leftPos + 1, i));
                    leftPos = -1;
                }
                parenthesisCount--;
            }
        }
        return commands.toArray(new String[0]);
    }

    /**
     * Checks if in the given string there is an open parenthesis or not.
     *
     * @param string: the string to be checked.
     *
     * @return parenthesisCount: the number of open parenthesis.
     */
    public Integer isOpenParenthesis(String string) {
        int parenthesisCount = 0;
        for (int i = 0; i < string.length(); i++) {
            char c = string.charAt(i);
            if (c == '(') parenthesisCount++;
            if (c == ')') parenthesisCount--;
        }
        return parenthesisCount;
    }

    /**
     * Removes the parenthesis from beginning and ending of the string.
     *
     * @param string: the string to be removed.
     *
     * @return string: the string stripped of parenthesis.
     */
    public String removeParenthesis(String string) {
        if (!string.contains("(") && !string.contains(")")) return string;
        string = string.startsWith("(") ? string.substring(1) : string;
        string = string.endsWith(")") ? string.substring(0, string.length() - 1) : string;
        return removeParenthesis(string);
    }

    /**
     * Converts a list into a string.
     *
     * @param list: the list that should be converted.
     * @param parseChatColor: enables translation of '&' into '§'
     *
     * @return message: the converted string.
     */
    public String getParsedMessage(List<String> list, Boolean parseChatColor) {
        String message = "";
        for (String string : list) message += (parseChatColor ? parseString(string) : string) + " ";
        return message.substring(0, message.length() - 1);
    }

    /**
     * Converts an array into a string.
     *
     * @param strings: the array that should be converted.
     * @param parseChatColor: enables translation of '&' into '§'
     *
     * @return message: the converted string.
     */
    public String getParsedMessage(String[] strings, Boolean parseChatColor) {
        String message = "";
        for (String string : strings) message += (parseChatColor ? parseString(string) : string) + " ";
        return message.substring(0, message.length() - 1);
    }

    public String repeat(char character, int times) {
        String string = "";
        for (int i = 0; i < times; ++i) string = string + character;
        return string;
    }

    /**
     * Converts '&' color code into '§'
     *
     * @param string: the string that should be converted.
     *
     * @return string: the converted string.
     */
    public String parseString(String string) {
        return string.replace("&", "§");
    }
}