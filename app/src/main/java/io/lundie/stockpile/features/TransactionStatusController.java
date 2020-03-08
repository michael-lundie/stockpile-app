package io.lundie.stockpile.features;

import androidx.annotation.NonNull;

import timber.log.Timber;

public class TransactionStatusController {

    private final EventPacket eventPacket = new EventPacket();

    //TODO: To be replaced with EventPacket. (belongs to old validation currently being used)
    private String eventMessage;

    public String getEventMessage() {
        String message = eventMessage;
        clearEventMessage();
        return message;
    }

    public EventPacket getEventPacket(@TransactionUpdateIdType.TransactionUpdateIdTypeDef int id) {
        if(id != 0 && this.eventPacket.getEventID() == id) {
            return eventPacket;
        } return null;
    }

    public void setEventMessage(String eventMessage) {
        this.eventMessage = eventMessage;
    }

    public void createEventPacket(@TransactionUpdateIdType.TransactionUpdateIdTypeDef  int eventID,
                                  String eventMessage, String fieldID) {
        this.eventPacket.setEventID(eventID);
        this.eventPacket.setEventMessage(eventMessage);
        this.eventPacket.setFieldID(fieldID);
    }

    public void createEventPacket(@TransactionUpdateIdType.TransactionUpdateIdTypeDef  int eventID,
                                  String eventMessage, int fieldID) {
        this.eventPacket.setEventID(eventID);
        this.eventPacket.setEventMessage(eventMessage);
        this.eventPacket.setFieldID(fieldID);
    }


    public boolean hasUpdate(@TransactionUpdateIdType.TransactionUpdateIdTypeDef int id) {
        return id != 0 && this.eventPacket.getEventID() == id;
    }

    public void clearEventPacket() {
        eventPacket.clear();
    }

    //TODO: To be replaced with EventPacket. (belongs to old validation currently being used)
    public void clearEventMessage() {
        this.eventMessage = null;
    }

    private void clearEvent() {

        Timber.e("Event Packet; clear called");
        eventPacket.clear();
    }

    public class EventPacket {
        private String eventMessage;
        private int eventID;
        private String stringFieldID;
        private int intFieldID;

        public String getEventMessage() {
            return eventMessage;
        }

        public void setEventMessage(String eventMessage) {
            this.eventMessage = eventMessage;
        }

        public int getEventID() {
            return eventID;
        }

        public String getStringFieldID() {
            return stringFieldID;
        }

        public int getIntFieldID() {
            return intFieldID;
        }

        private void setEventID(int eventID) {
            this.eventID = eventID;
        }

        private void setFieldID(int fieldID) {
            this.intFieldID = fieldID;
        }

        private void setFieldID(String fieldID) {
            this.stringFieldID = fieldID;
        }

        public void clear() {
            this.eventMessage = "";
            this.eventID = 0;
            stringFieldID = "";
            intFieldID = 0;
        }
    }
}
