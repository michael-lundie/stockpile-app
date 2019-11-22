package io.lundie.stockpile.utils.data;

import androidx.annotation.StringDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

public class CounterType {
    public static final String MILLILITRES = "ML";
    public static final String GRAMS = "Grams";
    public static final String NONE = "???";

    @StringDef({MILLILITRES, GRAMS, NONE})
    @Retention(RetentionPolicy.SOURCE)
    public @interface CounterTypeDef {}

    private String counterType;

    @CounterTypeDef
    public String getCounterType() {
        return this.counterType;
    }

}
