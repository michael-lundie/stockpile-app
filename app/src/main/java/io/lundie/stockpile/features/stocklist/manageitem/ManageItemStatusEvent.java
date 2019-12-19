package io.lundie.stockpile.features.stocklist.manageitem;

import io.lundie.stockpile.features.stocklist.manageitem.AddItemStatusType.AddItemStatusTypeDef;

class ManageItemStatusEvent {

    private @AddItemStatusTypeDef int errorStatus;
    private String eventText;

    int getErrorStatus() {
        return errorStatus;
    }

    void setErrorStatus(@AddItemStatusTypeDef int errorStatus) {
        this.errorStatus = errorStatus;
    }

    String getEventText() {
        return eventText;
    }

    void setEventText(String eventText) {
        this.eventText = eventText;
    }
}