package io.lundie.stockpile.data.model;

/**
 * Simple POJO class responsible for category data.
 * Note that this class requires an empty constructor for use with cloud firestore.
 */
public class CategoryCheckListItem {

    private String categoryName;
    private boolean checked;

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public boolean getIsChecked() {
        return checked;
    }

    public void setChecked(boolean checked) {
        this.checked = checked;
    }
}