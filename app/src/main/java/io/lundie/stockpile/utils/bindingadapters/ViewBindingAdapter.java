package io.lundie.stockpile.utils.bindingadapters;

import android.view.View;

import androidx.databinding.BindingAdapter;

import com.google.android.material.button.MaterialButton;

import java.util.Date;

import io.lundie.stockpile.R;
import io.lundie.stockpile.utils.DateUtils;
import timber.log.Timber;

/**
 * This class contains binding adapters which can be used with multiple view types and are
 * not view specific.
 */
public class ViewBindingAdapter {

    @BindingAdapter("visibility")
    public static void setVisibility(View view, Boolean visible) {
        Timber.e("Setting VIS --> %s, %s", view.getId(), visible);
        view.setVisibility(visible ? View.VISIBLE : View.GONE);
    }

    @BindingAdapter("visibility")
    public static void setVisibility(View view, String uri) {
        if(uri != null && !uri.isEmpty()) {
            view.setVisibility(View.VISIBLE);
        } else {
            view.setVisibility(View.GONE);
        }
    }

    @BindingAdapter("signInVisibility")
    public static void setSignInVisibility(View view, String string) {
        if(string != null && !string.isEmpty()) {
            view.setVisibility(View.GONE);
        } else {
            view.setVisibility(View.VISIBLE);
        }
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