package io.lundie.stockpile.data.model.internal;

/**
 * Simple pojo class specifically used for the display of category selection check lists in the
 * {@link io.lundie.stockpile.features.targets.ManageTargetsFragment}
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