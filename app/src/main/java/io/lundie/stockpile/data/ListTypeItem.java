package io.lundie.stockpile.data;

public class ListTypeItem {

    private String itemID;
    private String itemName;
    private String imageURI;
    private int totalItemsInPile;
    private int quantity;
    private int counter; // references a counter type
    private int expiring;



    public String getItemID() {
        return itemID;
    }

    public void setItemID(String itemID) {
        this.itemID = itemID;
    }

    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public String getImageURI() {
        return imageURI;
    }

    public void setImageURI(String imageURI) {
        this.imageURI = imageURI;
    }

    public int getTotalItemsInPile() {
        return totalItemsInPile;
    }

    public void setTotalItemsInPile(int totalItemsInPile) {
        this.totalItemsInPile = totalItemsInPile;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public int getCounter() {
        return counter;
    }

    public void setCounter(int counter) {
        this.counter = counter;
    }

    public int getExpiring() {
        return expiring;
    }

    public void setExpiring(int expiring) {
        this.expiring = expiring;
    }
}
