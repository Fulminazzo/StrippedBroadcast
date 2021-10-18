package it.fulminazzo.Utils;

public class NumberUtil {
    /**
     * Checks if a string is an integer.
     *
     * @param string: the string.
     *
     * @return boolean: if the string is an integer or not.
     */
    public static boolean isNumeric(String string) {
        try {
            Integer.parseInt(string);
            return true;
        } catch (NumberFormatException ignored) {
            return false;
        }
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
