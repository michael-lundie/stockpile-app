package io.lundie.stockpile.data.model.firestore;

/**
 * Simple POJO class responsible for category data.
 * Note that this class requires an clear constructor for use with cloud firestore.
 */
public class ItemCategory {

    private String categoryName;
    // int will adhere to hardcoded list of super types
    private int superType;
    private int numberOfPiles;
    private int totalCalories;
    private int iconUri;

    public ItemCategory() { /* Required clear constructor for Firestore */ }

    /**
     * This constructor is used only for creating fake data sets.
     * @param categoryName
     * @param superType
     * @param numberOfPiles
     * @param totalCalories
     * @param iconUri
     */
    public ItemCategory(String categoryName, int superType, int numberOfPiles, int totalCalories,
                        int iconUri) {
        setCategoryName(categoryName);
        setSuperType(superType);
        setNumberOfPiles(numberOfPiles);
        setTotalCalories(totalCalories);
        setIconUri(iconUri);
    }

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

    public int getIconUri() {
        return iconUri;
    }

    public void setIconUri(int iconUri) {this.iconUri = iconUri; }
}