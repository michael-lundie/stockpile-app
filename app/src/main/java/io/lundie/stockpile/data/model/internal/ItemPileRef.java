package io.lundie.stockpile.data.model.internal;

/**
 * A POJO class data model of an individual ItemPile.This reflects the model to be used by firestore.
 * Note that this class requires an clear constructor for use with cloud firestore.
 */
public class ItemPileRef {

    private String itemName;
    private String imagePath;
    private int itemCount;
    private int caloriesTotal;

    public ItemPileRef() { /* Required clear constructor for Firestore */ }

    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public int getItemCount() {
        return itemCount;
    }

    public void setItemCount(int itemCount) {
        this.itemCount = itemCount;
    }

    public int getCaloriesTotal() { return caloriesTotal; }

    public void setCaloriesTotal(int caloriesTotal) {
        this.caloriesTotal = caloriesTotal;
    }

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }
}