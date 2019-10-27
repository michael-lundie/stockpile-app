package io.lundie.stockpile.data;

import java.util.ArrayList;

public class UserData {

    private String userID;
    private String displayName;
    private ArrayList<ItemCategory> categories;

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

    public ArrayList<ItemCategory> getCategories() {
        return categories;
    }

    public void setCategories(ArrayList<ItemCategory> categories) {
        this.categories = categories;
    }
}
