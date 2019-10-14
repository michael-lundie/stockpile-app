package io.lundie.stockpile.features.user.models;

public class TypeDetails {

    private String typeName;
    private int superType;
    private int numberOfPiles;
    private int totalCalories;
    private String iconUri;

    public TypeDetails() {
        // Required Empty Constructor
    }

    public TypeDetails(String typeName, int superType,
                       int numberOfPiles, int totalCalories, String iconUri) {
        this.typeName = typeName;
        this.superType = superType;
        this.numberOfPiles = numberOfPiles;
        this.totalCalories = totalCalories;
        this.iconUri = iconUri;
    }

    public String getTypeName() {
        return typeName;
    }

    public void setTypeName(String typeName) {
        this.typeName = typeName;
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
