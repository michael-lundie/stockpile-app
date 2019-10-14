package io.lundie.stockpile.features.user.models;

import java.util.Map;

/**
 * Item POJO
 * TODO: Documentation
 */
public class User {

    private String displayName;
    private Map<String, TypeDetails> typeDetailsList;

    public User() {
        // Required Empty Constructor
    }

    public User(String displayName, Map<String, TypeDetails> typeDetailsList) {
        this.displayName = displayName;
        this.typeDetailsList = typeDetailsList;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public Map<String, TypeDetails> getTypeDetailsList() {
        return typeDetailsList;
    }

    public void setTypeDetailsList(Map<String, TypeDetails> typeDetailsList) {
        this.typeDetailsList = typeDetailsList;
    }
}
