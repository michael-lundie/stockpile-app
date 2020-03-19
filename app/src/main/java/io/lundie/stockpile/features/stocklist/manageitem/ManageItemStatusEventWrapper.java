package io.lundie.stockpile.features.stocklist.manageitem;

import io.lundie.stockpile.features.stocklist.manageitem.ImageUpdateStatusType.ImageUpdateStatusTypeDef;

/**
 * This is a wrapper class for {@link ImageUpdateStatusTypeDef}. Since LiveData cannot
 * contain primitive types and the use of {@link androidx.annotation.IntDef} is preferred over Enum
 * types in android a wrapper class is necessary.
 */
class ManageItemStatusEventWrapper {

    private @ImageUpdateStatusTypeDef
    int errorStatus;
    private String eventText;

    int getErrorStatus() {
        return errorStatus;
    }

    void setErrorStatus(@ImageUpdateStatusTypeDef int errorStatus) {
        this.errorStatus = errorStatus;
    }

    String getEventText() {
        return eventText;
    }

    void setEventText(String eventText) {
        this.eventText = eventText;
    }
}