package io.lundie.stockpile.features.targets;

import io.lundie.stockpile.utils.IntDefWrapper;

import static io.lundie.stockpile.features.targets.FrequencyTrackerType.FrequencyTrackerTypeDef;

/**
 * This is a wrapper class for {@link FrequencyTrackerType.FrequencyTrackerTypeDef}. Since LiveData cannot
 * contain primitive types and the use of {@link androidx.annotation.IntDef} is preferred over Enum
 * types in android a wrapper class is necessary.
 */
public class FrequencyTrackerTypeWrapper extends IntDefWrapper {

    FrequencyTrackerTypeWrapper(int typeDef) {
        super(typeDef);
    }

    @Override
    public void setTypeDef(@FrequencyTrackerTypeDef int typeDef) {
        super.setTypeDef(typeDef);
    }
}