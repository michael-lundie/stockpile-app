package io.lundie.stockpile.utils.bindingadapters;

import android.util.Log;
import android.view.View;

import androidx.databinding.BindingAdapter;

public class ProgressBarBindingAdapter {

    @BindingAdapter("visibility")
    public static void setVisibility(View view, Boolean visible) {

        view.setVisibility(visible ? View.VISIBLE : View.GONE);
        Log.e("vis adapter", "boolean:" + visible + "visibility: + " + view.getVisibility());
    }
}