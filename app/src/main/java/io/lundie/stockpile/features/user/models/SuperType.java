package io.lundie.stockpile.features.user;

public class SuperType {

    private String typeName;
    private String iconUri;

    public SuperType() {
        // Required Empty Constructor
    }

    public SuperType(String typeName, String iconUri) {
        this.typeName = typeName;
        this.iconUri = iconUri;
    }

    public String getTypeName() {
        return typeName;
    }

    public void setTypeName(String typeName) {
        this.typeName = typeName;
    }

    public String getIconUri() {
        return iconUri;
    }

    public void setIconUri(String iconUri) {
        this.iconUri = iconUri;
    }
}
