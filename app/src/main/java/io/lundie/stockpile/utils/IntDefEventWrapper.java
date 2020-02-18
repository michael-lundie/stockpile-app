package io.lundie.stockpile.utils;

/**
 * A simple extendable wrapper class for using {@link androidx.annotation.IntDef}
 * with LiveData.
 * IMPORTANT: Override get/set methods and add type definition annotations to their
 * input parameters.
 */
public abstract class IntDefEventWrapper extends IntDefWrapper {

    private String eventText;

    public String getEventText() {
        return eventText;
    }

    public void setEventText(String eventText) {
        this.eventText = eventText;
    }
}
