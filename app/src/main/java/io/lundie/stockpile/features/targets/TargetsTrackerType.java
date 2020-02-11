package io.lundie.stockpile.features.targets;

import androidx.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

public class TargetsTrackerType {
    public static final int NONE = 0;
    public static final int ITEMS = 1;
    public static final int CALORIES = 2;

    @IntDef({NONE, ITEMS, CALORIES})
    @Retention(RetentionPolicy.SOURCE)
    public @interface TargetsTrackerTypeDef {}

    private int target;

    public TargetsTrackerType(@TargetsTrackerTypeDef int target) {
        this.target = target;
    }
}
