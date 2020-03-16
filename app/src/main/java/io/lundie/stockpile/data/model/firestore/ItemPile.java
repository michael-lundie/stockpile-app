package io.lundie.stockpile.data.model.firestore;

import java.util.ArrayList;
import java.util.Date;

import static io.lundie.stockpile.utils.data.CounterType.*;

/**
 * A POJO class data model of an individual ItemPile.This reflects the model to be used by firestore.
 * Note that this class requires an clear constructor for use with cloud firestore.
 */
public class ItemPile {

    private String itemID;
    private String itemName;
    private String categoryName;
    private String imagePath;
    private int itemCount;
    private int calories;
    @CounterTypeDef
    private String counterType; // references a counterType type
    private int quantity;
    private ArrayList<Date> expiryList;

    public ItemPile() { /* Required clear constructor for Firestore */ }

    /**
     * Constructor : use only for building fake data sets.
     *
     * @param itemID
     * @param itemName
     * @param categoryName
     * @param imagePath
     * @param itemCount
     * @param calories
     * @param counterType
     * @param quantity
     * @param expiryList
     */
    public ItemPile(String itemID, String itemName, String categoryName, String imagePath,
                    int itemCount, int calories, @CounterTypeDef String counterType, int quantity,
                    ArrayList<Date> expiryList) {
        setItemID(itemID);
        setItemName(itemName);
        setCategoryName(categoryName);
        setImagePath(imagePath);
        setItemCount(itemCount);
        setCalories(calories);
        setCounterType(counterType);
        setQuantity(quantity);
        setExpiry(expiryList);
    }

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
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