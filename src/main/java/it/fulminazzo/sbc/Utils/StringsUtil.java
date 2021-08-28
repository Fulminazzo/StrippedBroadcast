package it.fulminazzo.sbc.Utils;

import it.fulminazzo.sbcAPI.PlayersUtil;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffectType;

import java.util.*;
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
     * Parses a single given command (for example "all" or "world=world_the_end").
     *
     * @param command: the command string.
     * @param player: the player that should return with keyword "me" (might be null).
     *
     * @return playersList: the list of all valid players according to command.
     */
    public List<Player> parseCommand(String command, Player player) {
        command = command.toLowerCase().replace(" ", "");
        if (command.replace(" ", "").equalsIgnoreCase("")) {
            return null;
        } else if (command.equalsIgnoreCase("all")) {
            // "all": returns all the online players.
            return PlayersUtil.getAllPlayers();
        } else if (command.equalsIgnoreCase("me")) {
            // "me": returns the sender of the command (if he is console, it will be null).
            return PlayersUtil.getUserPlayer(player);
        } else if (command.equalsIgnoreCase("op")) {
            // "op": returns every operator in the server.
            return PlayersUtil.getOPPlayers();
        } else if (command.startsWith("world=")) {
            // "world=<world>": returns every player from the world <world>.
            return PlayersUtil.getPlayersFromWorld(command);
        } else if (command.startsWith("perm=")) {
            // "perm=<permission>": returns every player from that has permission <permission>.
            return PlayersUtil.getPlayersFromPermission(command);
        } else if (command.startsWith("group=")) {
            // Only works with Luck Perms.
            // "group=<group>": returns every player that is in the group <group>.
            return PlayersUtil.getPlayersFromGroup(command);
        } else if (command.startsWith("item=")) {
            // "item=<item>,<amount>"
            return command.contains(",") ? PlayersUtil.getPlayersFromItem(command.split(",")[0], command.split(",")[1]) : PlayersUtil.getPlayersFromItem(command);
        } else if (command.startsWith("effect=")) {
            // "effect=<effect>,<amount>"
            return command.contains(",") ? PlayersUtil.getPlayersFromEffect(command.split(",")[0], command.split(",")[1]) : PlayersUtil.getPlayersFromEffect(command);
        } else if (command.startsWith("gamemode=")) {
            // "gamemode=<gamemode>"
            return PlayersUtil.getPlayersFromGameMode(command);
        } else if (command.startsWith("money=")) {
            // Only works with Vault.
            return PlayersUtil.getPlayersFromMoney(command);
        } else if (command.startsWith("player=") || Bukkit.getPlayer(command) != null) {
            // "player=<playerName>" or "<playerName>: returns the player called <playerName>
            return PlayersUtil.getPlayerFromName(command);
        }
        return null;
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

    /**
     * Repeats the given character a certain amount of times.
     *
     * @param character: the character to repeat.
     * @param times: the amount of times.
     *
     * @return string: a string containing the character repeated "times" times.
     */
    public String repeat(char character, int times) {
        String string = "";
        for (int i = 0; i < times; ++i) string += character;
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

    /**
     * Gets a list of all potion effect types names.
     *
     * @return list: the list.
     */
    public static List<String> getPotionEffects() {
        return Arrays.stream(PotionEffectType.values()).filter(Objects::nonNull).map(PotionEffectType::getName).collect(Collectors.toList());
    }

    /**
     * Checks if a string is valid for a general enum.
     *
     * @param enums: the enum values.
     * @param enumName: the enum name.
     *
     * @return boolean: if the enumName is contained in the enum values.
     */
    public static boolean isValidValue(Object[] enums, String enumName) {
        return isValidValue(Arrays.asList(enums), enumName);
    }

    /**
     * Checks if a string is valid for a general enum.
     *
     * @param enums: the enum values.
     * @param enumName: the enum name.
     *
     * @return boolean: if the enumName is contained in the enum values.
     */
    public static boolean isValidValue(List<Object> enums, String enumName) {
        return enums.stream().filter(Objects::nonNull).anyMatch(e -> e.toString().equalsIgnoreCase(enumName));
    }

    /**
     * Checks if a string is a double.
     *
     * @param string: the string.
     *
     * @return boolean: if the string is a double or not.
     */
    public static boolean isDouble(String string) {
        try {
            Double.parseDouble(string);
            return true;
        } catch (NumberFormatException ignored) {
            return false;
        }
    }
}