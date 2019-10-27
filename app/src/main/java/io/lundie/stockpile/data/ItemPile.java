package io.lundie.stockpile.data;

import java.util.ArrayList;
import java.util.Date;

/**
 * Item POJO
 * * TODO: Documentation
 */
public class ItemPile {

    private String itemID;
    private String itemName;
    private String categoryName;
    private String imageURI;
    private int itemCount;
    private int calories;
    private int counter; // references a counter type
    private int quantity;
    private ArrayList<Date> expiryList;

    public String getImageURI() {
        return imageURI;
    }

    public void setImageURI(String imageURI) {
        this.imageURI = imageURI;
    }

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

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public int getItemCount() {
        return itemCount;
    }

    public void setItemCount(int itemCount) {
        this.itemCount = itemCount;
    }

    public int getCalories() {
        return calories;
    }

    public void setCalories(int calories) {
        this.calories = calories;
    }

    public int getCounter() {
        return counter;
    }

    public void setCounter(int counter) {
        this.counter = counter;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public ArrayList<Date> getExpiry() {
        return expiryList;
    }

    public void setExpiry(ArrayList<Date> expiry) {
        this.expiryList = expiry;
    }
}
