package io.lundie.stockpile.features.stocklist.additem;

import io.lundie.stockpile.features.stocklist.additem.AddItemStatusType.AddItemStatusTypeDef;

public class AddItemStatusEvent {

    private @AddItemStatusTypeDef int errorStatus;
    private String eventText;

    public int getErrorStatus() {
        return errorStatus;
    }

    public void setErrorStatus(@AddItemStatusTypeDef int errorStatus) {
        this.errorStatus = errorStatus;
    }

    public String getEventText() {
        return eventText;
    }

    public void setEventText(String eventText) {
        this.eventText = eventText;
    }
}
