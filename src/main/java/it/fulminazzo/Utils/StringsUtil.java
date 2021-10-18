package it.fulminazzo.Utils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class StringsUtil {
    /**
     * Creates an array containing every command given in parentheses.
     * For example: "(world=world || world=world_the_end) && (perm=bukkit.*)"
     * becomes: ["world=world || world=world_the_end", "perm=bukkit"]
     *
     * @param command: the command string.
     *
     * @return commands: an array of commands.
     */
    public static String[] getCommandsFromParenthesis(String command) {
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
    public static Integer isOpenParenthesis(String string) {
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
    public static String removeParenthesis(String string) {
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
    public static String getParsedMessage(List<String> list, Boolean parseChatColor) {
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
    public static String getParsedMessage(String[] strings, Boolean parseChatColor) {
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
    public static String repeat(char character, int times) {
        String string = "";
        for (int i = 0; i < times; ++i) string += character;
        return string;
    }

    /**
     * Converts '&' color code into '§'.
     *
     * @param string: the string that should be converted.
     *
     * @return string: the converted string.
     */
    public static String parseString(String string) {
        return string.replace("&", "§");
    }

    /**
     * An alternative method of StringUtil#copyPartialMatches
     * from the Spigot API.
     */
    public static <T extends Collection<? super String>> T copyPartialMatches(String token, Iterable<String> originals, T collection) throws UnsupportedOperationException, IllegalArgumentException {
        for (String string : originals) if (string.toLowerCase().startsWith(token.toLowerCase())) collection.add(string);
        return collection;
    }
}
