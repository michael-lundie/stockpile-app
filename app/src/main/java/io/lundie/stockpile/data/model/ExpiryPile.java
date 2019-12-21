package io.lundie.stockpile.data.model;

/**
 * Simple pojo class specifically used for the display of expiry lists in the
 * {@link io.lundie.stockpile.features.stocklist.manageitem.ManageItemFragment}.
 * The addition of an item ID, allows us to use recycler view binding with a click listener.
 * We wouldn't be able to do this reliably otherwise (due to recycler view object ID's inherently
 * not being stable.
 */
public class ExpiryPile {
    private int itemCount;
    private String expiry;
    private int itemId;

    public ExpiryPile() { /* Required Empty Constructor for Firestore */ }

    public ExpiryPile(String expiryDate, int itemCount, int itemId) {
        this.expiry = expiryDate;
        this.itemCount = itemCount;
        this.itemId = itemId;
    }

    public int getItemCount() {
        return itemCount;
    }

    public void setItemCount(int itemCount) {
        this.itemCount = itemCount;
    }

    public String getExpiry() {
        return expiry;
    }

    public void setExpiry(String expiry) {
        this.expiry = expiry;
    }

    public int getItemId() {
        return itemId;
    }

    public void setItemId(int itemId) {
        this.itemId = itemId;
    }
}