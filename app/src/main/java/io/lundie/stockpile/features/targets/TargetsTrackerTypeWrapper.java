package io.lundie.stockpile.features.targets;

import io.lundie.stockpile.utils.IntDefWrapper;

import static io.lundie.stockpile.features.targets.TargetsTrackerType.*;

/**
 * This is a wrapper class for {@link TargetsTrackerTypeDef}. Since LiveData cannot
 * contain primitive types and the use of {@link androidx.annotation.IntDef} is preferred over Enum
 * types in android a wrapper class is necessary.
 */
public class TargetsTrackerTypeWrapper extends IntDefWrapper {

    TargetsTrackerTypeWrapper(@TargetsTrackerTypeDef int typeDef) {
        super(typeDef);
    }

    @Override
    public void setTypeDef(@TargetsTrackerTypeDef int typeDef) {
        super.setTypeDef(typeDef);
    }
}
