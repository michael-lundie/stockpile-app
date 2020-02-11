package io.lundie.stockpile.utils.bindingadapters;

import android.view.View;

import androidx.databinding.BindingAdapter;

import java.util.ArrayList;

import io.lundie.stockpile.data.model.CategoryCheckListItem;

/**
 * Binding Adapter responsible for {@link android.widget.CheckBox} binding in xml layout
 * fragment_targets_add_cat_item.xml . Allows the adapter item to "bind" to data directly in the
 * view model.
 * NOTES: There must be a better way to do this. Also using a HashMap would be much more performant
 * than the current ArrayList type.
 */
public class CheckboxBindingAdapters {
    @BindingAdapter({"isChecked", "categoryName"})
    public static void setAddTargetsCheckboxChecked(View view,
                                          ArrayList<CategoryCheckListItem> checkListItems,
                                          Object catName) {
        if(checkListItems != null) {
            for (CategoryCheckListItem item : checkListItems) {
                if(item.getCategoryName().equals(catName)) {
                    view.setSelected(item.getIsChecked());
                }
            }
        }
    }
}
