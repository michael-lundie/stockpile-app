package io.lundie.stockpile.utils;

public class AppUtils {

    public static String validateInput(String regex, String input,
                                       int minLength, String errorInvalidCharacters) {
        if(input == null) return null;
        if (hasInvalidCharacters(regex, input)) {
            return errorInvalidCharacters;
        }

        if (minLength != 0) {
            if (minLength == 1 && input.length() < 1) {
                //Todo: fix string literal
                return "Required";
            } else if (input.length() < minLength) {
                return "Minimum length: " + minLength;
            }
        }
        return null;
    }

    public static boolean hasInvalidCharacters(String regex, String string) {
        String filteredString = string.replaceAll(regex, "");
        return !string.equals(filteredString);
    }
}
