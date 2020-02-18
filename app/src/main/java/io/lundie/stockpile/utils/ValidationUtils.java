package io.lundie.stockpile.utils;

import io.lundie.stockpile.utils.ValidationUtilsErrorType.ValidationUtilsErrorTypeDef;
import timber.log.Timber;

import static io.lundie.stockpile.utils.ValidationUtilsErrorType.*;

public class ValidationUtils {

    public static final String specialCharsRegEx = "[\\p{P}\\p{S}]";
    public static final String numbersRegEx = "[^0-9]";


    public static @ValidationUtilsErrorTypeDef int validateInput(String regex, String input, int minLength) {
        if(input == null) return NULL_INPUT;
        if (hasInvalidCharacters(regex, input)) {
            return INVALID_CHARS;
        }
        Timber.e("Input length is %s", input.length());
        if (minLength != 0) {
            if (minLength == 1 && input.length() < 1) {

                return EMPTY_FIELD;
            } else if (input.length() < minLength) {
                return MIN_NOT_REACHED;
            }
        }
        Timber.e("Returning null from validator");
        return VALID;
    }

    public static boolean hasInvalidCharacters(String regex, String string) {
        String filteredString = string.replaceAll(regex, "");
        return !string.equals(filteredString);
    }
}
