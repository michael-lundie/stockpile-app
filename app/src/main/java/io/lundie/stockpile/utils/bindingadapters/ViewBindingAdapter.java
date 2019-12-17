package io.lundie.stockpile.utils.bindingadapters;

import android.view.View;

import androidx.databinding.BindingAdapter;

public class ViewBindingAdapter {

    @BindingAdapter("visibility")
    public static void setVisibility(View view, Boolean visible) {
        view.setVisibility(visible ? View.VISIBLE : View.GONE);
    }
}