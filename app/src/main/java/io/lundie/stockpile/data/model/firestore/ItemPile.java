package io.lundie.stockpile.data.model.firestore;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.Date;

import static io.lundie.stockpile.utils.data.CounterType.*;
import static java.lang.System.in;

/**
 * A POJO class data model of an individual ItemPile.This reflects the model to be used by firestore.
 * Note that this class requires an clear constructor for use with cloud firestore.
 */
public class ItemPile implements Parcelable {

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

    public ItemPile(Parcel in) {
        this.itemID = in.readString();
        this.itemName = in.readString();
        this.categoryName = in.readString();
        this.imagePath = in.readString();
        this.itemCount = in.readInt();
        this.calories = in.readInt();
        this.counterType = in.readString();
        this.quantity = in.readInt();
        this.expiryList = in.readArrayList(Date.class.getClassLoader());
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel out, int flags) {
        out.writeString(itemID);
        out.writeString(itemName);
        out.writeString(categoryName);
        out.writeString(imagePath);
        out.writeInt(itemCount);
        out.writeInt(calories);
        out.writeString(counterType);
        out.writeInt(quantity);
        out.writeList(expiryList);
    }

    public static final Parcelable.Creator<ItemPile> CREATOR = new Parcelable.Creator<ItemPile>() {
        @Override
        public ItemPile createFromParcel(Parcel in) {
            return new ItemPile(in);
        }

        @Override
        public ItemPile[] newArray(int size) {
            return new ItemPile[size];
        }
    };

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