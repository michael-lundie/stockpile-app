package io.lundie.stockpile.utils.bindingadapters;

import android.view.View;

import androidx.databinding.BindingAdapter;

import com.google.android.material.button.MaterialButton;

import java.util.Date;

import io.lundie.stockpile.R;
import io.lundie.stockpile.utils.DateUtils;

public class ViewBindingAdapter {

    @BindingAdapter("visibility")
    public static void setVisibility(View view, Boolean visible) {
        view.setVisibility(visible ? View.VISIBLE : View.GONE);
    }

    @BindingAdapter("expiring_icon")
    public static void setIsExpiringIcon(MaterialButton view, Object date) {
        if(date != null) {
            if(DateUtils.isDateWithinRange((Date) date)) {
                view.setIconResource(R.drawable.ic_warning_accent_24dp);
                view.setIconTintResource(R.color.color_accent);
            } else {
                view.setIconResource(R.drawable.ic_check_circle_primary_24dp);
                view.setIconTintResource(R.color.color_primary);
            }

        }
    }
}