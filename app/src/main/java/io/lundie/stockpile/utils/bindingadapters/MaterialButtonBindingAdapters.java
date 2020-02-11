package io.lundie.stockpile.utils.bindingadapters;

import android.view.View;

import androidx.databinding.BindingAdapter;

import io.lundie.stockpile.utils.IntDefWrapper;

public class MaterialButtonBindingAdapters {

    @BindingAdapter({"setIsSelected", "viewType"})
    public static void setStyle(View view, IntDefWrapper currentSelected, int viewType) {
        int currentlySelected = currentSelected.getTypeDef();
        if(currentlySelected != 0) {
            if(currentlySelected == viewType) {
                view.setSelected(true);
            } else {
                view.setSelected(false);
            }
        } else {
            view.setSelected(false);
        }
    }
}
