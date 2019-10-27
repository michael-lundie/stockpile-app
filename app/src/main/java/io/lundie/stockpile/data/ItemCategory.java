package io.lundie.stockpile.data;

/**
 * Item POJO
 * TODO: Documentation
 */
public class ItemCategory {

    private String categoryName;
    private int superType; // int will adhere to hardcoded list of super types
    private int numberOfPiles;
    private int totalCalories;
    private String iconUri;

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public int getSuperType() {
        return superType;
    }

    public void setSuperType(int superType) {
        this.superType = superType;
    }

    public int getNumberOfPiles() {
        return numberOfPiles;
    }

    public void setNumberOfPiles(int numberOfPiles) {
        this.numberOfPiles = numberOfPiles;
    }

    public int getTotalCalories() {
        return totalCalories;
    }

    public void setTotalCalories(int totalCalories) {
        this.totalCalories = totalCalories;
    }

    public String getIconUri() {
        return iconUri;
    }

    public void setIconUri(String iconUri) {
        this.iconUri = iconUri;
    }
}