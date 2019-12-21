package io.lundie.stockpile.data.model;

import java.util.ArrayList;
import java.util.Date;

import io.lundie.stockpile.utils.data.CounterType;

import static io.lundie.stockpile.utils.data.CounterType.*;

/**
 * A POJO class data model of an individual ItemPile.
 */
public class ItemPile {

    private String itemID;
    private String itemName;
    private String categoryName;
    private String imageURI;
    private int itemCount;
    private int calories;
    @CounterTypeDef
    private String counterType; // references a counterType type
    private int quantity;
    private ArrayList<Date> expiryList;

    public ItemPile() { /* Required empty constructor for Firestore */ }

    /**
     * Constructor : use only for building fake data sets.
     *
     * @param itemID
     * @param itemName
     * @param categoryName
     * @param imageURI
     * @param itemCount
     * @param calories
     * @param counterType
     * @param quantity
     * @param expiryList
     */
    public ItemPile(String itemID, String itemName, String categoryName, String imageURI,
                    int itemCount, int calories, @CounterTypeDef String counterType, int quantity,
                    ArrayList<Date> expiryList) {
        setItemID(itemID);
        setItemName(itemName);
        setCategoryName(categoryName);
        setImageURI(imageURI);
        setItemCount(itemCount);
        setCalories(calories);
        setCounterType(counterType);
        setQuantity(quantity);
        setExpiry(expiryList);
    }

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

    public String getCategoryName() { return categoryName; }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public int getItemCount() {
        return itemCount;
    }

    public void setItemCount(int itemCount) {
        this.itemCount = itemCount;
    }

    public int getCalories() { return calories; }

    public void setCalories(int calories) {
        this.calories = calories;
    }

    @CounterTypeDef
    public String getCounterType() {
        return counterType;
    }

    public void setCounterType(@CounterTypeDef String counterType) {
        this.counterType = counterType;
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