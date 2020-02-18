package io.lundie.stockpile.utils;

import androidx.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

public class ValidationUtilsErrorType {
    public static final int NULL_INPUT = 0;
    public static final int EMPTY_FIELD = 1;
    public static final int INVALID_CHARS = 2;
    public static final int MIN_NOT_REACHED = 3;
    public static final int VALID = 4;

    @IntDef({NULL_INPUT, EMPTY_FIELD, INVALID_CHARS, MIN_NOT_REACHED, VALID})
    @Retention(RetentionPolicy.SOURCE)
    public @interface ValidationUtilsErrorTypeDef {}

    private int validationError;

    public ValidationUtilsErrorType(@ValidationUtilsErrorTypeDef int validationError) {
        this.validationError = validationError;
    }
}
