package io.lundie.stockpile.data.model.firestore;

import java.util.ArrayList;

/**
 * Simple POJO class responsible for User Data. This model reflects the data model to be used
 * by firestore.
 * Note that this class requires an clear constructor for use with cloud firestore.
 */
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
        if(displayName == null) {
            return "";
        }
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
