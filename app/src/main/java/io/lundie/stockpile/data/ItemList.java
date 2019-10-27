package io.lundie.stockpile.data;

import java.util.ArrayList;

public class ItemList {

    private String categoryName;
    private ArrayList<ListTypeItem> listItems;

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public ArrayList<ListTypeItem> getListItems() {
        return listItems;
    }

    public void setListItems(ArrayList<ListTypeItem> listItems) {
        this.listItems = listItems;
    }
}
