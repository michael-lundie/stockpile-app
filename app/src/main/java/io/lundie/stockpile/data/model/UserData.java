package io.lundie.stockpile.data.model;

import java.util.ArrayList;

public class UserData {

    private String userID;
    private String displayName;
    private String email;
    private ArrayList<ItemCategory> categories;
    private ArrayList<Target> targets;

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getEmail() { return email; }

    public void setEmail(String email) { this.email = email; }

    public ArrayList<ItemCategory> getCategories() {
        return categories;
    }

    public void setCategories(ArrayList<ItemCategory> categories) {
        this.categories = categories;
    }

    public ArrayList<Target> getTargets() { return targets; }

    public void setTargets(ArrayList<Target> targets) { this.targets = targets; }
}
