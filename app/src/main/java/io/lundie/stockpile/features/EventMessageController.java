package io.lundie.stockpile.features;

public class EventMessageController {

    private String eventMessage;
    private int eventID;

    public String getEventMessage() {
        String message = eventMessage;
        eventMessage = null;
        return message;
    }

    public void setEventMessage(String eventMessage) {
        this.eventMessage = eventMessage;
    }

    public int getEventID() {
        return eventID;
    }

    public void setEventID(int eventID) {
        this.eventID = eventID;
    }

    public void clearEventMessage() {
        this.eventMessage = null;
        this.eventID = 0;
    }
}
