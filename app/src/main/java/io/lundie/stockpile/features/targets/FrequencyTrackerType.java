package io.lundie.stockpile.features.targets;

import androidx.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

public class FrequencyTrackerType {
    public static final int NONE = 0;
    public static final int WEEKLY = 1;
    public static final int MONTHLY = 2;

    @IntDef({NONE, WEEKLY, MONTHLY})
    @Retention(RetentionPolicy.SOURCE)
    public @interface FrequencyTrackerTypeDef {}

    private int frequency;

    public FrequencyTrackerType(@FrequencyTrackerTypeDef int frequency) {
        this.frequency = frequency;
    }
}
