package io.lundie.stockpile.data.model;

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
