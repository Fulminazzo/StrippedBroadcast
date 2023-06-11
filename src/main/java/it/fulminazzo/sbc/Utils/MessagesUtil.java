package it.fulminazzo.sbc.Utils;

import it.fulminazzo.Utils.StringsUtil;
import it.fulminazzo.sbcAPI.PlayersUtil;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffectType;

import java.util.*;
import java.util.stream.Collectors;

public class MessagesUtil {
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

}